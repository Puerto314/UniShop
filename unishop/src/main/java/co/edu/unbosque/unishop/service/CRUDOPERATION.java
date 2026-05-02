package co.edu.unbosque.unishop.service;

public interface CRUDOPERATION<T> {

	public int create(T data);

	public String getAll();

	public int deleteById(Long id);

	public long count();

	public boolean exist(Long id);

	public int updateById(Long id, T data);

}
