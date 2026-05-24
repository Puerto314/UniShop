package co.edu.unbosque.unishop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.unishop.dto.AdminDTO;
import co.edu.unbosque.unishop.service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080" })
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * GET /admin/mostrartodo
     * CORRECCIÓN: siempre devuelve 200 OK aunque la lista esté vacía
     * (antes devolvía 204 No Content con cuerpo nulo, lo que rompía el frontend).
     */
    @GetMapping("/mostrartodo")
    public ResponseEntity<List<AdminDTO>> mostrarTodo() {
        List<AdminDTO> lista = adminService.getAll();
        return ResponseEntity.ok(lista);
    }

    /**
     * POST /admin/crear?nombreUsuario=xxx&contraseniaUsuario=yyy
     * El codigoAdmin siempre se asigna automáticamente como "Puerto314".
     */
    @PostMapping("/crear")
    public ResponseEntity<String> crear(
            @RequestParam String nombreUsuario,
            @RequestParam String contraseniaUsuario) {
        int resultado = adminService.create(nombreUsuario, contraseniaUsuario);
        return switch (resultado) {
            case 1  -> new ResponseEntity<>("Admin creado correctamente", HttpStatus.CREATED);
            case -1 -> new ResponseEntity<>("El nombre de usuario '" + nombreUsuario + "' ya existe", HttpStatus.CONFLICT);
            default -> new ResponseEntity<>("Error al crear admin", HttpStatus.BAD_REQUEST);
        };
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizar(
            @PathVariable Long id,
            @RequestParam String nombreUsuario,
            @RequestParam String contraseniaUsuario) {
        int resultado = adminService.updateById(id, nombreUsuario, contraseniaUsuario);
        return switch (resultado) {
            case 1  -> new ResponseEntity<>("Admin actualizado", HttpStatus.OK);
            case -1 -> new ResponseEntity<>("El nombre de usuario '" + nombreUsuario + "' ya existe", HttpStatus.CONFLICT);
            default -> new ResponseEntity<>("Admin no encontrado", HttpStatus.NOT_FOUND);
        };
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        int resultado = adminService.deleteById(id);
        if (resultado == 1) {
            return new ResponseEntity<>("Admin eliminado", HttpStatus.OK);
        }
        return new ResponseEntity<>("Admin no encontrado", HttpStatus.NOT_FOUND);
    }
}
