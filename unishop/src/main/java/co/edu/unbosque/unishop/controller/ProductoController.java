package co.edu.unbosque.unishop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import co.edu.unbosque.unishop.service.ProductoService;

@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "*" })
public class ProductoController {

	@Autowired
	private ProductoService productoService;

}
