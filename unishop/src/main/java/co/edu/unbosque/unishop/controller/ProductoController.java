package co.edu.unbosque.unishop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.unishop.dto.AmazonItemDTO;
import co.edu.unbosque.unishop.dto.AmazonReviewDTO;
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

    // ── /producto/mostrartodo ─────────────────────────────────────────────
    @GetMapping("/mostrartodo")
    public ResponseEntity<List<ProductoDTO>> mostrarTodo() {
        List<ProductoDTO> productos = productoService.getAll();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(productos, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    // ── /producto/buscar?nombre=iphone ───────────────────────────────────
    /**
     * Devuelve hasta 10 productos de Amazon.com con precio real en USD.
     *
     * Respuesta JSON:
     * {
     *   "amazon": [
     *     { "asin", "title", "price", "url", "imageUrl", "rating", "reviewCount" }
     *   ]
     * }
     */
    @GetMapping("/buscar")
    public ResponseEntity<RespuestaExternaDTO> buscarEnTiendas(
            @RequestParam(name = "nombre") String nombreProducto) {

        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<AmazonItemDTO> items =
                ManipuladorDeSolicitudesHTTPExternas.buscarEnAmazon(nombreProducto);

        return new ResponseEntity<>(new RespuestaExternaDTO(items), HttpStatus.OK);
    }

    // ── /producto/resenas?asin=B0CHX3QBCH ───────────────────────────────
    /**
     * Devuelve hasta 10 reseñas de un producto de Amazon dado su ASIN.
     *
     * Respuesta JSON:
     * [
     *   { "author", "title", "body", "rating", "date" }
     * ]
     */
    @GetMapping("/resenas")
    public ResponseEntity<List<AmazonReviewDTO>> obtenerResenas(
            @RequestParam(name = "asin") String asin) {

        if (asin == null || asin.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<AmazonReviewDTO> resenas =
                ManipuladorDeSolicitudesHTTPExternas.obtenerResenasAmazon(asin.trim());

        if (resenas.isEmpty()) {
            return new ResponseEntity<>(resenas, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(resenas, HttpStatus.OK);
    }
}
