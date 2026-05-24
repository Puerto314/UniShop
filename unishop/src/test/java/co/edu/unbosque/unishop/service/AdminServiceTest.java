package co.edu.unbosque.unishop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import co.edu.unbosque.unishop.dto.AdminDTO;
import co.edu.unbosque.unishop.entity.Admin;
import co.edu.unbosque.unishop.repository.AdminRepository;

/**
 * Pruebas unitarias para AdminService.
 *
 * ✅ SEGURIDAD DE DATOS:
 *    - Todos los repositorios son MOCKS (Mockito): NUNCA tocan la base de datos real.
 *    - No se persiste, modifica ni elimina ningún dato real.
 *    - Cada test es completamente aislado e independiente.
 */
@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AdminService adminService;

    // ── Datos de prueba reutilizables ─────────────────────────────────────
    private Admin adminMock;
    private AdminDTO adminDTOMock;

    @BeforeEach
    void setUp() {
        // Entidad Admin ficticia — nunca se guarda en BD real
        adminMock = new Admin("usuarioTest", "pass123", "Puerto314");
        adminMock.setId(1L);

        // DTO Admin ficticio
        adminDTOMock = new AdminDTO("usuarioTest", "pass123", "Puerto314");
        adminDTOMock.setId(1L);
    }

    // ══════════════════════════════════════════════════════════════════════
    // CREATE
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void create_cuandoNombreNoExiste_retorna1() {
        // El nombre no existe en BD (mock)
        when(adminRepository.existsByNombreUsuario("usuarioNuevo")).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        int resultado = adminService.create("usuarioNuevo", "pass123");

        assertEquals(1, resultado, "Debe retornar 1 cuando el admin se crea exitosamente");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void create_cuandoNombreYaExiste_retornaMenos1() {
        // El nombre ya existe en BD (mock)
        when(adminRepository.existsByNombreUsuario("usuarioTest")).thenReturn(true);

        int resultado = adminService.create("usuarioTest", "pass123");

        assertEquals(-1, resultado, "Debe retornar -1 cuando el nombre de usuario ya existe");
        // Nunca debe intentar guardar en BD
        verify(adminRepository, never()).save(any(Admin.class));
    }

    // ══════════════════════════════════════════════════════════════════════
    // GET ALL
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void getAll_cuandoHayAdmins_retornaLista() {
        Admin admin2 = new Admin("admin2", "pass2", "Puerto314");
        admin2.setId(2L);
        AdminDTO dto2 = new AdminDTO("admin2", "pass2", "Puerto314");

        when(adminRepository.findAll()).thenReturn(Arrays.asList(adminMock, admin2));
        when(mapper.map(adminMock, AdminDTO.class)).thenReturn(adminDTOMock);
        when(mapper.map(admin2, AdminDTO.class)).thenReturn(dto2);

        List<AdminDTO> lista = adminService.getAll();

        assertNotNull(lista, "La lista no debe ser nula");
        assertEquals(2, lista.size(), "Debe retornar 2 admins");
    }

    @Test
    void getAll_cuandoNoHayAdmins_retornaListaVacia() {
        when(adminRepository.findAll()).thenReturn(Arrays.asList());

        List<AdminDTO> lista = adminService.getAll();

        assertNotNull(lista, "La lista no debe ser nula");
        assertTrue(lista.isEmpty(), "La lista debe estar vacía");
    }

    // ══════════════════════════════════════════════════════════════════════
    // DELETE BY ID
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void deleteById_cuandoExiste_retorna1() {
        when(adminRepository.existsById(1L)).thenReturn(true);
        doNothing().when(adminRepository).deleteById(1L);

        int resultado = adminService.deleteById(1L);

        assertEquals(1, resultado, "Debe retornar 1 cuando el admin existe y se elimina");
        verify(adminRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_cuandoNoExiste_retorna0() {
        when(adminRepository.existsById(99L)).thenReturn(false);

        int resultado = adminService.deleteById(99L);

        assertEquals(0, resultado, "Debe retornar 0 cuando el admin no existe");
        verify(adminRepository, never()).deleteById(anyLong());
    }

    // ══════════════════════════════════════════════════════════════════════
    // UPDATE BY ID
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void updateById_cuandoExisteYNombreLibre_retorna1() {
        when(adminRepository.existsById(1L)).thenReturn(true);
        when(adminRepository.existsByNombreUsuario("nuevoNombre")).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        int resultado = adminService.updateById(1L, "nuevoNombre", "nuevaPass");

        assertEquals(1, resultado, "Debe retornar 1 cuando se actualiza correctamente");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void updateById_cuandoNoExiste_retorna0() {
        when(adminRepository.existsById(99L)).thenReturn(false);

        int resultado = adminService.updateById(99L, "cualquierNombre", "pass");

        assertEquals(0, resultado, "Debe retornar 0 cuando el admin no existe");
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void updateById_cuandoNombreUsadoPorOtro_retornaMenos1() {
        // El ID 1L existe
        when(adminRepository.existsById(1L)).thenReturn(true);
        // El nombre "nombreOcupado" existe en BD
        when(adminRepository.existsByNombreUsuario("nombreOcupado")).thenReturn(true);

        // Pero pertenece a otro admin (ID = 2L, no 1L)
        Admin otroAdmin = new Admin("nombreOcupado", "pass", "Puerto314");
        otroAdmin.setId(2L);
        when(adminRepository.findByNombreUsuario("nombreOcupado")).thenReturn(Optional.of(otroAdmin));

        int resultado = adminService.updateById(1L, "nombreOcupado", "pass");

        assertEquals(-1, resultado, "Debe retornar -1 cuando el nombre ya lo usa otro admin");
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void updateById_cuandoNombreEsElMismoDelAdmin_retorna1() {
        // El admin 1L quiere actualizar solo su contraseña, manteniendo el mismo nombre
        when(adminRepository.existsById(1L)).thenReturn(true);
        when(adminRepository.existsByNombreUsuario("usuarioTest")).thenReturn(true);

        // El nombre "usuarioTest" le pertenece al mismo admin (ID = 1L)
        when(adminRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(adminMock));
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        int resultado = adminService.updateById(1L, "usuarioTest", "nuevaPass");

        assertEquals(1, resultado, "Debe retornar 1 cuando el nombre es del mismo admin");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    // ══════════════════════════════════════════════════════════════════════
    // COUNT / EXIST
    // ══════════════════════════════════════════════════════════════════════

    @Test
    void count_retornaCantidadCorrecta() {
        when(adminRepository.count()).thenReturn(3L);

        long total = adminService.count();

        assertEquals(3L, total, "Debe retornar la cantidad correcta de admins");
    }

    @Test
    void exist_cuandoExiste_retornaTrue() {
        when(adminRepository.existsById(1L)).thenReturn(true);

        assertTrue(adminService.exist(1L), "Debe retornar true cuando el admin existe");
    }

    @Test
    void exist_cuandoNoExiste_retornaFalse() {
        when(adminRepository.existsById(999L)).thenReturn(false);

        assertFalse(adminService.exist(999L), "Debe retornar false cuando el admin no existe");
    }
}