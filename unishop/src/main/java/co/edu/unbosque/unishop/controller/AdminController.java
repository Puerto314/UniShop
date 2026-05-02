package co.edu.unbosque.unishop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import co.edu.unbosque.unishop.service.AdminService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8080", "*" })
public class AdminController {

	@Autowired
	private AdminService adminService;

}
