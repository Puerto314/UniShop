package co.edu.unbosque.unishop.controller;

import co.edu.unbosque.unishop.dto.CrearOrdenRequestDTO;
import co.edu.unbosque.unishop.dto.OrdenCompraDTO;
import co.edu.unbosque.unishop.service.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080" })
public class OrdenCompraController {

    @Autowired
    private OrdenCompraService ordenService;

    /**
     * POST /ordenes
     * El cliente autenticado crea un nuevo pedido.
     * Requiere rol USER o ADMIN.
     */
    @PostMapping
    public ResponseEntity<String> crearOrden(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CrearOrdenRequestDTO dto) {

        int result = ordenService.crearOrden(userDetails.getUsername(), dto);
        if (result == 1) {
            return new ResponseEntity<>("Orden guardada", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
    }

    /**
     * GET /ordenes/mias
     * Devuelve el historial de pedidos del cliente autenticado.
     * Requiere rol USER o ADMIN.
     */
    @GetMapping("/mias")
    public ResponseEntity<List<OrdenCompraDTO>> getMisOrdenes(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<OrdenCompraDTO> lista = ordenService.getOrdenesPropias(userDetails.getUsername());
        return ResponseEntity.ok(lista);
    }

    /**
     * GET /ordenes/todas
     * Devuelve TODOS los pedidos de todos los clientes.
     * Solo accesible para ADMIN (protegido en SecurityConfig).
     */
    @GetMapping("/todas")
    public ResponseEntity<List<OrdenCompraDTO>> getTodasLasOrdenes() {
        return ResponseEntity.ok(ordenService.getTodasLasOrdenes());
    }
}
