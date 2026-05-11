package co.edu.unbosque.unishop.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.unishop.dto.ProductoDTO;
import co.edu.unbosque.unishop.entity.Producto;
import co.edu.unbosque.unishop.repository.ProductoRepository;

@Service
public class ProductoService implements CRUDOPERATION<ProductoDTO> {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private ModelMapper mapper;

	public ProductoService() {
	}

	@Override
	public int create(ProductoDTO data) {
		Producto producto = mapper.map(data, Producto.class);
		productoRepository.save(producto);
		return 1;
	}

	@Override
	public List<ProductoDTO> getAll() {
		List<ProductoDTO> lista = new ArrayList<>();
		productoRepository.findAll().forEach(p -> lista.add(mapper.map(p, ProductoDTO.class)));
		return lista;
	}

	@Override
	public int deleteById(Long id) {
		if (!productoRepository.existsById(id)) return 0;
		productoRepository.deleteById(id);
		return 1;
	}

	@Override
	public long count() {
		return productoRepository.count();
	}

	@Override
	public boolean exist(Long id) {
		return productoRepository.existsById(id);
	}

	@Override
	public int updateById(Long id, ProductoDTO data) {
		if (!productoRepository.existsById(id)) return 0;
		Producto producto = mapper.map(data, Producto.class);
		producto.setId(id);
		productoRepository.save(producto);
		return 1;
	}

}
