package co.edu.unbosque.unishop.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unbosque.unishop.dto.ProductoDTO;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exist(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int updateById(Long id, ProductoDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

}
