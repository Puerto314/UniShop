package co.edu.unbosque.unishop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.unishop.dto.ClienteDTO;
import co.edu.unbosque.unishop.service.ClienteService;

@RestController
@RequestMapping("/cliente")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "*" })
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping("/mostrartodo")
	public ResponseEntity<List<ClienteDTO>> mostrarTodo() {
		List<ClienteDTO> lista = clienteService.getAll();
		if (lista.isEmpty()) {
			return new ResponseEntity<>(lista, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	/**
	 * POST /cliente/crear?nombreUsuario=xxx&contraseniaUsuario=yyy&correoElectronico=zzz
	 * - Rechaza nombre duplicado (409 CONFLICT)
	 * - Valida formato de correo (400 BAD REQUEST)
	 * - Si todo es valido: crea el cliente y envia codigo de verificacion al correo
	 */
	@PostMapping("/crear")
	public ResponseEntity<String> crear(
			@RequestParam String nombreUsuario,
			@RequestParam String contraseniaUsuario,
			@RequestParam String correoElectronico) {
		int resultado = clienteService.create(nombreUsuario, contraseniaUsuario, correoElectronico);
		return switch (resultado) {
			case 1  -> new ResponseEntity<>(
						"Cliente creado correctamente. Se envio un codigo de verificacion a " + correoElectronico,
						HttpStatus.CREATED);
			case -1 -> new ResponseEntity<>(
						"El nombre de usuario '" + nombreUsuario + "' ya existe",
						HttpStatus.CONFLICT);
			case -2 -> new ResponseEntity<>(
						"El correo electronico '" + correoElectronico + "' no tiene un formato valido",
						HttpStatus.BAD_REQUEST);
			default -> new ResponseEntity<>("Error al crear cliente", HttpStatus.BAD_REQUEST);
		};
	}

	@PutMapping("/actualizar/{id}")
	public ResponseEntity<String> actualizar(
			@PathVariable Long id,
			@RequestParam String nombreUsuario,
			@RequestParam String contraseniaUsuario,
			@RequestParam String correoElectronico) {
		int resultado = clienteService.updateById(id, nombreUsuario, contraseniaUsuario, correoElectronico);
		return switch (resultado) {
			case 1  -> new ResponseEntity<>("Cliente actualizado", HttpStatus.OK);
			case -1 -> new ResponseEntity<>(
						"El nombre de usuario '" + nombreUsuario + "' ya existe",
						HttpStatus.CONFLICT);
			case -2 -> new ResponseEntity<>(
						"El correo electronico '" + correoElectronico + "' no tiene un formato valido",
						HttpStatus.BAD_REQUEST);
			default -> new ResponseEntity<>("Cliente no encontrado", HttpStatus.NOT_FOUND);
		};
	}

	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		int resultado = clienteService.deleteById(id);
		if (resultado == 1) {
			return new ResponseEntity<>("Cliente eliminado", HttpStatus.OK);
		}
		return new ResponseEntity<>("Cliente no encontrado", HttpStatus.NOT_FOUND);
	}

}
