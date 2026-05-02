package co.edu.unbosque.unishop.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.unbosque.unishop.dto.AdminDTO;
import co.edu.unbosque.unishop.repository.AdminRepository;

@Service
public class AdminService implements CRUDOPERATION<AdminDTO> {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private ModelMapper mapper;

	public AdminService() {

	}

	@Override
	public int create(AdminDTO data) {
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
	public int updateById(Long id, AdminDTO data) {
		// TODO Auto-generated method stub
		return 0;
	}

}
