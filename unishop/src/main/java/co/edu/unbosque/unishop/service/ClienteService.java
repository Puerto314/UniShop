package co.edu.unbosque.unishop.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unbosque.unishop.dto.ClienteDTO;
import co.edu.unbosque.unishop.repository.ClienteRepository;

@Service
public class ClienteService implements CRUDOPERATION<ClienteDTO> {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ModelMapper mapper;

	public ClienteService() {

	}

	@Override
	public int create(ClienteDTO data) {
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
	public int updateById(Long id, ClienteDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

}
