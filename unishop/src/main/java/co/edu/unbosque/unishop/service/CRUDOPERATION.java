package co.edu.unbosque.unishop.service;

import java.util.List;

public interface CRUDOPERATION<T> {

	public int create(T data);

	public List<T> getAll();

	public int deleteById(Long id);

	public long count();

	public boolean exist(Long id);

	public int updateById(Long id, T data);

}
