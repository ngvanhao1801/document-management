package com.example.demo.repository;

import com.example.demo.dto.ChartDTO;
import com.example.demo.entity.Document;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query(value = "SELECT * FROM document AS d WHERE d.folder_id = ? AND d.status_id = 3", nativeQuery = true)
	public List<Document> listDocumentByFolder(Long folderId);

	// Top 10 product by category
	@Query(value = "SELECT * FROM document AS b WHERE b.folder_id = ?;", nativeQuery = true)
	List<Document> listDocumentByFolder10(Long folderId);

	// List product new
	@Query(value = "SELECT * FROM document ORDER BY upload_date DESC limit 10;", nativeQuery = true)
	public List<Document> listDocumentNew20();

	// Search Product
	@Query(value = "SELECT * FROM document WHERE document_name LIKE %?1%", nativeQuery = true)
	public List<Document> searchDocument(String documentName);

	@Query(value = "SELECT f.folder_id, f.folder_name, " +
			"COUNT(*) AS SoLuong " +
			"FROM document d " +
			"JOIN folder f ON d.folder_id = f.folder_id " +
			"WHERE d.status_id = 3 " +
			"GROUP BY f.folder_name;" , nativeQuery = true)
	List<Object[]> listFolderByDocumentName();

	// count quantity by product
	@Query(value = "SELECT f.folder_id,f.folder_name,\r\n"
			+ "COUNT(*) AS SoLuong\r\n"
			+ "FROM document d\r\n"
			+ "JOIN folder f ON d.folder_id = f.folder_id\r\n"
			+ "GROUP BY f.folder_name;", nativeQuery = true)
	List<Object[]> listFolderByFolderName();

	// Top 20 product best sale
	@Query(value = "SELECT p.product_id,\n"
			+ "COUNT(*) AS SoLuong\r\n"
			+ "FROM order_details p\r\n"
			+ "JOIN products c ON p.product_id = c.product_id\r\n"
			+ "GROUP BY p.product_id\r\n"
			+ "ORDER by SoLuong DESC limit 20;", nativeQuery = true)
	public List<Object[]> bestSaleDocument20();

	@Query(value = "select * from products o where product_id in :ids", nativeQuery = true)
	List<Product> findByInventoryIds(@Param("ids") List<Integer> listProductId);

	@Query(name = "getDocumentFavourite", nativeQuery = true)
	List<ChartDTO> getDocumentByFavorite();

	@Query(value = "select * from document d where d.user_id = ?1", nativeQuery = true)
	List<Document> getListDocumentUpload(Long userId);

	@Query(value = "SELECT COUNT(d.id) FROM document d WHERE d.user_id = ?1", nativeQuery = true)
	int countDocumentUploadByUser(Long userId);

	@Query(value = "select * from document d where d.status_id = 3;", nativeQuery = true)
	List<Document> findAllByDocumentStatus();

	@Modifying
	@Transactional
	@Query(value = "UPDATE document d SET d.views = d.views + 1 WHERE d.id = :id", nativeQuery = true)
	void incrementViews(@Param("id") Long id);

}
