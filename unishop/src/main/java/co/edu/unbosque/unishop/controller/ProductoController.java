package co.edu.unbosque.unishop.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.unishop.dto.AmazonItemDTO;
import co.edu.unbosque.unishop.dto.ProductoDTO;
import co.edu.unbosque.unishop.dto.RespuestaExternaDTO;
import co.edu.unbosque.unishop.service.ManipuladorDeSolicitudesHTTPExternas;
import co.edu.unbosque.unishop.service.ProductoService;

@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Value("${mercadolibre.client-id}")
    private String mlClientId;

    /**
     * GET /producto/mostrartodo
     */
    @GetMapping("/mostrartodo")
    public ResponseEntity<List<ProductoDTO>> mostrarTodo() {
        List<ProductoDTO> productos = productoService.getAll();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(productos, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * GET /producto/buscar?nombre=celular+xiaomi
     *
     * - Amazon: se busca desde el servidor (scraping).
     * - MercadoLibre: el servidor devuelve la URL lista para que el
     *   FRONTEND la consuma directamente. Los servidores cloud/ngrok
     *   reciben 403 de ML; el browser del usuario no.
     *
     * Respuesta JSON:
     * {
     *   "mercadoLibre":    [],
     *   "mercadoLibreUrl": "https://api.mercadolibre.com/sites/MCO/search?q=...",
     *   "amazon":          [ { nombreProducto, descripcionProducto, precioProducto, tienda } ]
     * }
     */
    @GetMapping("/buscar")
    public ResponseEntity<RespuestaExternaDTO> buscarEnTiendas(
            @RequestParam(name = "nombre") String nombreProducto) {

        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // ── Amazon (scraping desde el servidor) ───────────────────────────────
        List<AmazonItemDTO> itemsAmazon =
                ManipuladorDeSolicitudesHTTPExternas.buscarEnAmazon(nombreProducto);

        List<ProductoDTO> productosAmazon = new ArrayList<>();
        for (AmazonItemDTO item : itemsAmazon) {
            String url = item.getUrl() != null
                    ? item.getUrl()
                    : "https://www.amazon.com.co/dp/" + item.getAsin();
            ProductoDTO dto = new ProductoDTO(
                    item.getTitle(),
                    url,
                    item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO,
                    "Amazon"
            );
            productosAmazon.add(dto);
        }

        // ── MercadoLibre URL (el frontend la consume directamente) ────────────
        String query = URLEncoder.encode(nombreProducto.trim(), StandardCharsets.UTF_8);
        String mlUrl = "https://api.mercadolibre.com/sites/MCO/search?q="
                     + query + "&limit=10&sort=relevance";

        RespuestaExternaDTO respuesta = new RespuestaExternaDTO(new ArrayList<>(), productosAmazon);
        respuesta.setMercadoLibreUrl(mlUrl);

        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
