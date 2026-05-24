package co.edu.unbosque.unishop.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.unishop.dto.AdminDTO;
import co.edu.unbosque.unishop.dto.ClienteDTO;
import co.edu.unbosque.unishop.dto.ProductoDTO;
import co.edu.unbosque.unishop.service.AdminService;
import co.edu.unbosque.unishop.service.ClienteService;
import co.edu.unbosque.unishop.service.ProductoService;


@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock private AdminService   adminService;
    @Mock private ClienteService clienteService;
    @Mock private ProductoService productoService;

    @InjectMocks private AdminController   adminController;
    @InjectMocks private ClienteController clienteController;
    @InjectMocks private ProductoController productoController;

    private AdminDTO adminDTO;
    private ClienteDTO clienteDTO;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        adminDTO = new AdminDTO("adminTest", "pass123", "Puerto314");
        adminDTO.setId(1L);

        clienteDTO = new ClienteDTO("clienteTest", "pass123", "cliente@mail.com");
        clienteDTO.setId(1L);

        productoDTO = new ProductoDTO("Laptop", "Laptop gaming", new BigDecimal("1500.00"), "Amazon");
        productoDTO.setId(1L);
    }

  
    @Test
    void admin_mostrarTodo_cuandoHayAdmins_retornaOK() {
        when(adminService.getAll()).thenReturn(Arrays.asList(adminDTO));

        ResponseEntity<List<AdminDTO>> resp = adminController.mostrarTodo();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void admin_mostrarTodo_cuandoNoHayAdmins_retornaNoContent() {
        when(adminService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<AdminDTO>> resp = adminController.mostrarTodo();

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test
    void admin_crear_cuandoExito_retornaCreated() {
        when(adminService.create("adminNuevo", "pass")).thenReturn(1);

        ResponseEntity<String> resp = adminController.crear("adminNuevo", "pass");

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertTrue(resp.getBody().contains("correctamente"));
    }

    @Test
    void admin_crear_cuandoNombreDuplicado_retornaConflict() {
        when(adminService.create("adminExistente", "pass")).thenReturn(-1);

        ResponseEntity<String> resp = adminController.crear("adminExistente", "pass");

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void admin_crear_cuandoErrorInesperado_retornaBadRequest() {
        when(adminService.create("admin", "pass")).thenReturn(0);

        ResponseEntity<String> resp = adminController.crear("admin", "pass");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void admin_actualizar_cuandoExito_retornaOK() {
        when(adminService.updateById(1L, "nuevoNombre", "nuevaPass")).thenReturn(1);

        ResponseEntity<String> resp = adminController.actualizar(1L, "nuevoNombre", "nuevaPass");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_actualizar_cuandoNombreDuplicado_retornaConflict() {
        when(adminService.updateById(1L, "ocupado", "pass")).thenReturn(-1);

        ResponseEntity<String> resp = adminController.actualizar(1L, "ocupado", "pass");

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void admin_actualizar_cuandoNoExiste_retornaNotFound() {
        when(adminService.updateById(99L, "nombre", "pass")).thenReturn(0);

        ResponseEntity<String> resp = adminController.actualizar(99L, "nombre", "pass");

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void admin_eliminar_cuandoExiste_retornaOK() {
        when(adminService.deleteById(1L)).thenReturn(1);

        ResponseEntity<String> resp = adminController.eliminar(1L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void admin_eliminar_cuandoNoExiste_retornaNotFound() {
        when(adminService.deleteById(99L)).thenReturn(0);

        ResponseEntity<String> resp = adminController.eliminar(99L);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

 
    @Test
    void cliente_mostrarTodo_cuandoHayClientes_retornaOK() {
        when(clienteService.getAll()).thenReturn(Arrays.asList(clienteDTO));

        ResponseEntity<List<ClienteDTO>> resp = clienteController.mostrarTodo();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void cliente_mostrarTodo_cuandoNoHayClientes_retornaNoContent() {
        when(clienteService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ClienteDTO>> resp = clienteController.mostrarTodo();

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }

    @Test
    void cliente_crear_cuandoExito_retornaCreated() {
        when(clienteService.create("clienteNuevo", "pass", "nuevo@mail.com")).thenReturn(1);

        ResponseEntity<String> resp = clienteController.crear("clienteNuevo", "pass", "nuevo@mail.com");

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertTrue(resp.getBody().contains("correctamente"));
    }

    @Test
    void cliente_crear_cuandoNombreDuplicado_retornaConflict() {
        when(clienteService.create("existente", "pass", "correo@mail.com")).thenReturn(-1);

        ResponseEntity<String> resp = clienteController.crear("existente", "pass", "correo@mail.com");

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void cliente_crear_cuandoCorreoInvalido_retornaBadRequest() {
        when(clienteService.create("nombre", "pass", "correoMalo")).thenReturn(-2);

        ResponseEntity<String> resp = clienteController.crear("nombre", "pass", "correoMalo");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void cliente_crear_cuandoErrorInesperado_retornaBadRequest() {
        when(clienteService.create("nombre", "pass", "correo@mail.com")).thenReturn(0);

        ResponseEntity<String> resp = clienteController.crear("nombre", "pass", "correo@mail.com");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void cliente_actualizar_cuandoExito_retornaOK() {
        when(clienteService.updateById(1L, "nuevoNombre", "nuevaPass", "nuevo@mail.com")).thenReturn(1);

        ResponseEntity<String> resp = clienteController.actualizar(
                1L, "nuevoNombre", "nuevaPass", "nuevo@mail.com");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void cliente_actualizar_cuandoNombreDuplicado_retornaConflict() {
        when(clienteService.updateById(1L, "ocupado", "pass", "correo@mail.com")).thenReturn(-1);

        ResponseEntity<String> resp = clienteController.actualizar(1L, "ocupado", "pass", "correo@mail.com");

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void cliente_actualizar_cuandoCorreoInvalido_retornaBadRequest() {
        when(clienteService.updateById(1L, "nombre", "pass", "malo")).thenReturn(-2);

        ResponseEntity<String> resp = clienteController.actualizar(1L, "nombre", "pass", "malo");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void cliente_actualizar_cuandoNoExiste_retornaNotFound() {
        when(clienteService.updateById(99L, "nombre", "pass", "correo@mail.com")).thenReturn(0);

        ResponseEntity<String> resp = clienteController.actualizar(99L, "nombre", "pass", "correo@mail.com");

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void cliente_eliminar_cuandoExiste_retornaOK() {
        when(clienteService.deleteById(1L)).thenReturn(1);

        ResponseEntity<String> resp = clienteController.eliminar(1L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void cliente_eliminar_cuandoNoExiste_retornaNotFound() {
        when(clienteService.deleteById(99L)).thenReturn(0);

        ResponseEntity<String> resp = clienteController.eliminar(99L);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

  
    @Test
    void producto_mostrarTodo_cuandoHayProductos_retornaOK() {
        when(productoService.getAll()).thenReturn(Arrays.asList(productoDTO));

        ResponseEntity<List<ProductoDTO>> resp = productoController.mostrarTodo();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void producto_mostrarTodo_cuandoNoHayProductos_retornaNoContent() {
        when(productoService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProductoDTO>> resp = productoController.mostrarTodo();

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    }
}