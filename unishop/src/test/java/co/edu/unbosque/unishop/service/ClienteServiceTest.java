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

import co.edu.unbosque.unishop.dto.ClienteDTO;
import co.edu.unbosque.unishop.entity.Cliente;
import co.edu.unbosque.unishop.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private ModelMapper mapper;

	@Mock
	private CorreoService correoService;

	@InjectMocks
	private ClienteService clienteService;

	private Cliente clienteMock;
	private ClienteDTO clienteDTOMock;

	private static final String NOMBRE = "clienteTest";
	private static final String PASSWORD = "pass123";
	private static final String CORREO = "cliente@correo.com";

	@BeforeEach
	void setUp() {
		clienteMock = new Cliente(NOMBRE, PASSWORD, CORREO);
		clienteMock.setId(1L);

		clienteDTOMock = new ClienteDTO(NOMBRE, PASSWORD, CORREO);
		clienteDTOMock.setId(1L);
	}

	@Test
	void create_cuandoDatosValidos_retorna1() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);
		when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMock);
		when(correoService.generarCodigo()).thenReturn("123456");
		when(correoService.enviarCodigoVerificacion(CORREO, "123456")).thenReturn(true);

		int resultado = clienteService.create(NOMBRE, PASSWORD, CORREO);

		assertEquals(1, resultado, "Debe retornar 1 cuando el cliente se crea exitosamente");
		verify(clienteRepository, times(1)).save(any(Cliente.class));
		verify(correoService, times(1)).enviarCodigoVerificacion(eq(CORREO), anyString());
	}

	@Test
	void create_cuandoNombreYaExiste_retornaMenos1() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(true);

		int resultado = clienteService.create(NOMBRE, PASSWORD, CORREO);

		assertEquals(-1, resultado, "Debe retornar -1 cuando el nombre de usuario ya existe");
		verify(clienteRepository, never()).save(any(Cliente.class));
		verify(correoService, never()).enviarCodigoVerificacion(anyString(), anyString());
	}

	@Test
	void create_cuandoCorreoSinArroba_retornaMenos2() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);

		int resultado = clienteService.create(NOMBRE, PASSWORD, "correosindominio.com");

		assertEquals(-2, resultado, "Debe retornar -2 cuando el correo no tiene '@'");
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void create_cuandoCorreoSinDominio_retornaMenos2() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);

		int resultado = clienteService.create(NOMBRE, PASSWORD, "correo@sinpunto");

		assertEquals(-2, resultado, "Debe retornar -2 cuando el dominio no tiene punto");
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void create_cuandoCorreoNulo_retornaMenos2() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);

		int resultado = clienteService.create(NOMBRE, PASSWORD, null);

		assertEquals(-2, resultado, "Debe retornar -2 cuando el correo es nulo");
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void create_cuandoCorreoVacio_retornaMenos2() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);

		int resultado = clienteService.create(NOMBRE, PASSWORD, "   ");

		assertEquals(-2, resultado, "Debe retornar -2 cuando el correo está en blanco");
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void create_cuandoCorreoArrobaAlInicio_retornaMenos2() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);

		int resultado = clienteService.create(NOMBRE, PASSWORD, "@dominio.com");

		assertEquals(-2, resultado, "Debe retornar -2 cuando '@' está al inicio del correo");
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void create_cuandoCorreoValido_seEnviaCorreoMock() {
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(false);
		when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMock);
		when(correoService.generarCodigo()).thenReturn("654321");
		when(correoService.enviarCodigoVerificacion(CORREO, "654321")).thenReturn(false); // simula fallo de correo

		int resultado = clienteService.create(NOMBRE, PASSWORD, CORREO);

		assertEquals(1, resultado, "Debe retornar 1 aunque el correo no se haya podido enviar");
		verify(clienteRepository, times(1)).save(any(Cliente.class));
	}

	@Test
	void getAll_cuandoHayClientes_retornaLista() {
		Cliente c2 = new Cliente("otro", "pass2", "otro@mail.com");
		c2.setId(2L);
		ClienteDTO dto2 = new ClienteDTO("otro", "pass2", "otro@mail.com");

		when(clienteRepository.findAll()).thenReturn(Arrays.asList(clienteMock, c2));
		when(mapper.map(clienteMock, ClienteDTO.class)).thenReturn(clienteDTOMock);
		when(mapper.map(c2, ClienteDTO.class)).thenReturn(dto2);

		List<ClienteDTO> lista = clienteService.getAll();

		assertNotNull(lista);
		assertEquals(2, lista.size(), "Debe retornar 2 clientes");
	}

	@Test
	void getAll_cuandoNoHayClientes_retornaListaVacia() {
		when(clienteRepository.findAll()).thenReturn(Arrays.asList());

		List<ClienteDTO> lista = clienteService.getAll();

		assertNotNull(lista);
		assertTrue(lista.isEmpty(), "La lista debe estar vacía");
	}

	@Test
	void deleteById_cuandoExiste_retorna1() {
		when(clienteRepository.existsById(1L)).thenReturn(true);
		doNothing().when(clienteRepository).deleteById(1L);

		int resultado = clienteService.deleteById(1L);

		assertEquals(1, resultado);
		verify(clienteRepository, times(1)).deleteById(1L);
	}

	@Test
	void deleteById_cuandoNoExiste_retorna0() {
		when(clienteRepository.existsById(99L)).thenReturn(false);

		int resultado = clienteService.deleteById(99L);

		assertEquals(0, resultado);
		verify(clienteRepository, never()).deleteById(anyLong());
	}

	@Test
	void updateById_cuandoExisteYDatosValidos_retorna1() {
		when(clienteRepository.existsById(1L)).thenReturn(true);
		when(clienteRepository.existsByNombreUsuario("nuevoNombre")).thenReturn(false);
		when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMock);

		int resultado = clienteService.updateById(1L, "nuevoNombre", "nuevaPass", "nuevo@mail.com");

		assertEquals(1, resultado);
		verify(clienteRepository, times(1)).save(any(Cliente.class));
	}

	@Test
	void updateById_cuandoNoExiste_retorna0() {
		when(clienteRepository.existsById(99L)).thenReturn(false);

		int resultado = clienteService.updateById(99L, NOMBRE, PASSWORD, CORREO);

		assertEquals(0, resultado);
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void updateById_cuandoNombreUsadoPorOtro_retornaMenos1() {
		when(clienteRepository.existsById(1L)).thenReturn(true);
		when(clienteRepository.existsByNombreUsuario("nombreOcupado")).thenReturn(true);

		Cliente otroCliente = new Cliente("nombreOcupado", "pass", "otro@mail.com");
		otroCliente.setId(2L);
		when(clienteRepository.findByNombreUsuario("nombreOcupado")).thenReturn(Optional.of(otroCliente));

		int resultado = clienteService.updateById(1L, "nombreOcupado", PASSWORD, CORREO);

		assertEquals(-1, resultado);
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void updateById_cuandoNombreEsDelMismoCliente_retorna1() {
		when(clienteRepository.existsById(1L)).thenReturn(true);
		when(clienteRepository.existsByNombreUsuario(NOMBRE)).thenReturn(true);
		when(clienteRepository.findByNombreUsuario(NOMBRE)).thenReturn(Optional.of(clienteMock));
		when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteMock);

		int resultado = clienteService.updateById(1L, NOMBRE, "nuevaPass", CORREO);

		assertEquals(1, resultado, "Debe permitir mantener el mismo nombre de usuario");
	}

	@Test
	void updateById_cuandoCorreoInvalido_retornaMenos2() {
		when(clienteRepository.existsById(1L)).thenReturn(true);
		when(clienteRepository.existsByNombreUsuario("nuevoNombre")).thenReturn(false);

		int resultado = clienteService.updateById(1L, "nuevoNombre", PASSWORD, "correoMalo");

		assertEquals(-2, resultado, "Debe retornar -2 cuando el correo tiene formato inválido");
		verify(clienteRepository, never()).save(any(Cliente.class));
	}

	@Test
	void count_retornaCantidadCorrecta() {
		when(clienteRepository.count()).thenReturn(5L);

		assertEquals(5L, clienteService.count());
	}

	@Test
	void exist_cuandoExiste_retornaTrue() {
		when(clienteRepository.existsById(1L)).thenReturn(true);

		assertTrue(clienteService.exist(1L));
	}

	@Test
	void exist_cuandoNoExiste_retornaFalse() {
		when(clienteRepository.existsById(999L)).thenReturn(false);

		assertFalse(clienteService.exist(999L));
	}
}