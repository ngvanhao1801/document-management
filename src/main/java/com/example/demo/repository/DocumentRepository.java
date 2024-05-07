package com.example.demo.repository;

import com.example.demo.dto.ChartDTO;
import com.example.demo.entity.Document;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query(value = "SELECT * FROM document WHERE folder_id = ?", nativeQuery = true)
	public List<Product> listDocumentByFolder(Long folderId);

	// Top 10 product by category
	@Query(value = "SELECT * FROM document AS b WHERE b.folder_id = ?;", nativeQuery = true)
	List<Product> listDocumentByFolder10(Long folderId);

	// List product new
	@Query(value = "SELECT * FROM document ORDER BY upload_date DESC limit 20;", nativeQuery = true)
	public List<Product> listDocumentNew20();

	// Search Product
	@Query(value = "SELECT * FROM document WHERE documentName LIKE %?1%", nativeQuery = true)
	public List<Product> searchDocument(String documentName);

	// count quantity by product
	@Query(value = "SELECT f.folder_id,f.folder_name,\r\n"
			+ "COUNT(*) AS SoLuong\r\n"
			+ "FROM document d\r\n"
			+ "JOIN folder f ON d.folder_id = f.folder_id\r\n"
			+ "GROUP BY f.folder_name;", nativeQuery = true)
	List<Object[]> listFolderByFolderName();

	// Top 20 product best sale
	@Query(value = "SELECT p.product_id,\r\n"
			+ "COUNT(*) AS SoLuong\r\n"
			+ "FROM order_details p\r\n"
			+ "JOIN products c ON p.product_id = c.product_id\r\n"
			+ "GROUP BY p.product_id\r\n"
			+ "ORDER by SoLuong DESC limit 20;", nativeQuery = true)
	public List<Object[]> bestSaleProduct20();

	@Query(value = "select * from products o where product_id in :ids", nativeQuery = true)
	List<Product> findByInventoryIds(@Param("ids") List<Integer> listProductId);

	@Query(name = "getProductOrders", nativeQuery = true)
	List<ChartDTO> getProductOrders(Pageable pageable, Integer moth, Integer year);

}
