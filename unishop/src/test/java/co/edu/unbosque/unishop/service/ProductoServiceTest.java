package co.edu.unbosque.unishop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import co.edu.unbosque.unishop.dto.ProductoDTO;
import co.edu.unbosque.unishop.entity.Producto;
import co.edu.unbosque.unishop.repository.ProductoRepository;


@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoMock;
    private ProductoDTO productoDTOMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto("Laptop", "Laptop gaming", new BigDecimal("1500.00"), "Amazon");
        productoMock.setId(1L);

        productoDTOMock = new ProductoDTO("Laptop", "Laptop gaming", new BigDecimal("1500.00"), "Amazon");
        productoDTOMock.setId(1L);
    }

 
    @Test
    void create_cuandoDatosValidos_retorna1() {
        when(mapper.map(productoDTOMock, Producto.class)).thenReturn(productoMock);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        int resultado = productoService.create(productoDTOMock);

        assertEquals(1, resultado, "Debe retornar 1 cuando el producto se crea exitosamente");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void create_siempreRetorna1() {
        ProductoDTO otroDTO = new ProductoDTO("Mouse", "Mouse inalámbrico", new BigDecimal("25.00"), "Amazon");
        Producto otroProducto = new Producto("Mouse", "Mouse inalámbrico", new BigDecimal("25.00"), "Amazon");

        when(mapper.map(otroDTO, Producto.class)).thenReturn(otroProducto);
        when(productoRepository.save(any(Producto.class))).thenReturn(otroProducto);

        int resultado = productoService.create(otroDTO);

        assertEquals(1, resultado);
    }

 
    @Test
    void getAll_cuandoHayProductos_retornaLista() {
        Producto p2 = new Producto("Teclado", "Teclado mecánico", new BigDecimal("80.00"), "Amazon");
        p2.setId(2L);
        ProductoDTO dto2 = new ProductoDTO("Teclado", "Teclado mecánico", new BigDecimal("80.00"), "Amazon");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoMock, p2));
        when(mapper.map(productoMock, ProductoDTO.class)).thenReturn(productoDTOMock);
        when(mapper.map(p2, ProductoDTO.class)).thenReturn(dto2);

        List<ProductoDTO> lista = productoService.getAll();

        assertNotNull(lista, "La lista no debe ser nula");
        assertEquals(2, lista.size(), "Debe retornar 2 productos");
    }

    @Test
    void getAll_cuandoNoHayProductos_retornaListaVacia() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList());

        List<ProductoDTO> lista = productoService.getAll();

        assertNotNull(lista);
        assertTrue(lista.isEmpty(), "La lista debe estar vacía");
    }

    @Test
    void getAll_primerElementoTieneNombreCorrecto() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoMock));
        when(mapper.map(productoMock, ProductoDTO.class)).thenReturn(productoDTOMock);

        List<ProductoDTO> lista = productoService.getAll();

        assertEquals("Laptop", lista.get(0).getNombreProducto(),
                "El primer producto debe llamarse 'Laptop'");
    }


    @Test
    void deleteById_cuandoExiste_retorna1() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        int resultado = productoService.deleteById(1L);

        assertEquals(1, resultado, "Debe retornar 1 cuando el producto existe y se elimina");
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_cuandoNoExiste_retorna0() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        int resultado = productoService.deleteById(99L);

        assertEquals(0, resultado, "Debe retornar 0 cuando el producto no existe");
        verify(productoRepository, never()).deleteById(anyLong());
    }


    @Test
    void updateById_cuandoExiste_retorna1() {
        ProductoDTO datosActualizados = new ProductoDTO(
                "Laptop Pro", "Laptop gaming pro", new BigDecimal("2000.00"), "Amazon");
        Producto productoActualizado = new Producto(
                "Laptop Pro", "Laptop gaming pro", new BigDecimal("2000.00"), "Amazon");

        when(productoRepository.existsById(1L)).thenReturn(true);
        when(mapper.map(datosActualizados, Producto.class)).thenReturn(productoActualizado);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

        int resultado = productoService.updateById(1L, datosActualizados);

        assertEquals(1, resultado, "Debe retornar 1 cuando el producto existe y se actualiza");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void updateById_cuandoNoExiste_retorna0() {
        ProductoDTO datosActualizados = new ProductoDTO(
                "Laptop", "desc", new BigDecimal("100.00"), "Amazon");

        when(productoRepository.existsById(99L)).thenReturn(false);

        int resultado = productoService.updateById(99L, datosActualizados);

        assertEquals(0, resultado, "Debe retornar 0 cuando el producto no existe");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void updateById_asignaIdCorrectamente() {
        ProductoDTO datosActualizados = new ProductoDTO(
                "Monitor", "Monitor 4K", new BigDecimal("500.00"), "Amazon");
        Producto productoSinId = new Producto("Monitor", "Monitor 4K", new BigDecimal("500.00"), "Amazon");

        when(productoRepository.existsById(5L)).thenReturn(true);
        when(mapper.map(datosActualizados, Producto.class)).thenReturn(productoSinId);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            assertEquals(5L, p.getId(), "El ID debe asignarse correctamente antes de guardar");
            return p;
        });

        int resultado = productoService.updateById(5L, datosActualizados);

        assertEquals(1, resultado);
    }


    @Test
    void count_retornaCantidadCorrecta() {
        when(productoRepository.count()).thenReturn(10L);

        assertEquals(10L, productoService.count(), "Debe retornar la cantidad correcta de productos");
    }

    @Test
    void exist_cuandoExiste_retornaTrue() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        assertTrue(productoService.exist(1L), "Debe retornar true cuando el producto existe");
    }

    @Test
    void exist_cuandoNoExiste_retornaFalse() {
        when(productoRepository.existsById(999L)).thenReturn(false);

        assertFalse(productoService.exist(999L), "Debe retornar false cuando el producto no existe");
    }
}