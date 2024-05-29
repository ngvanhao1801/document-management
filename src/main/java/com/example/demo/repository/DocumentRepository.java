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
	@Query(value = "SELECT * FROM document AS b WHERE b.status_id = 3 and b.folder_id = ?;", nativeQuery = true)
	List<Document> listDocumentByFolder10(Long folderId);

	// List product new
	@Query(value = "SELECT * FROM document d where d.status_id = 3 ORDER BY d.upload_date DESC limit 10;", nativeQuery = true)
	public List<Document> listDocumentNew20();

	// Search Product
	@Query(value = "SELECT * FROM document d WHERE d.status_id = 3 and d.document_name LIKE %?1%", nativeQuery = true)
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

	@Query(value = "SELECT \n" +
			"    year(d.upload_date) AS year,\n" +
			"    COUNT(*) AS totalDocuments,\n" +
			"    SUM(CASE WHEN d.favorite = true THEN 1 ELSE 0 END) AS favoriteDocuments,\n" +
			"    SUM(d.views) AS totalViews,\n" +
			"    (\n" +
			"        SELECT media_type\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        GROUP BY media_type\n" +
			"        ORDER BY COUNT(*) DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS popularMediaType,\n" +
			"    COUNT(CASE WHEN d.status_id = 3 THEN 1 ELSE NULL END) AS completedDocuments,\n" +
			"    COUNT(CASE WHEN d.status_id = 1 THEN 1 ELSE NULL END) AS inProgressDocuments,\n" +
			"    COUNT(CASE WHEN d.status_id = 2 THEN 1 ELSE NULL END) AS deletedDocuments,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        ORDER BY upload_date DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS latestDocument,\n" +
			"    (\n" +
			"        SELECT user_id\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        GROUP BY user_id\n" +
			"        ORDER BY COUNT(*) DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostActiveUser,\n" +
			"    (\n" +
			"        SELECT GROUP_CONCAT(DISTINCT u.name)\n" +
			"        FROM document doc\n" +
			"        INNER JOIN `user` u ON doc.user_id = u.user_id\n" +
			"        WHERE year(doc.upload_date) = year(d.upload_date)\n" +
			"    ) AS userNames,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        ORDER BY views DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostViewedDocument\n" +
			"FROM document d\n" +
			"GROUP BY year(d.upload_date)\n" +
			"ORDER BY year(d.upload_date);", nativeQuery = true)
	List<Object[]> listReportYearCommon();

	@Query(value = "SELECT \n" +
			"    year(d.upload_date) AS year,\n" +
			"    month(d.upload_date) AS month,\n" +
			"    COUNT(*) AS totalDocuments,\n" +
			"    SUM(CASE WHEN d.favorite = true THEN 1 ELSE 0 END) AS favoriteDocuments,\n" +
			"    SUM(d.views) AS totalViews,\n" +
			"    (\n" +
			"        SELECT media_type\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND month(upload_date) = month(d.upload_date)\n" +
			"        GROUP BY media_type\n" +
			"        ORDER BY COUNT(*) DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS popularMediaType,\n" +
			"    COUNT(CASE WHEN d.status_id = 3 THEN 1 ELSE NULL END) AS completedDocuments,\n" +
			"    COUNT(CASE WHEN d.status_id = 1 THEN 1 ELSE NULL END) AS inProgressDocuments,\n" +
			"    COUNT(CASE WHEN d.status_id = 2 THEN 1 ELSE NULL END) AS deletedDocuments,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND month(upload_date) = month(d.upload_date)\n" +
			"        ORDER BY upload_date DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS latestDocument,\n" +
			"    (\n" +
			"        SELECT user_id\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND month(upload_date) = month(d.upload_date)\n" +
			"        GROUP BY user_id\n" +
			"        ORDER BY COUNT(*) DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostActiveUser,\n" +
			"    GROUP_CONCAT(DISTINCT u.name) AS userNames,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND month(upload_date) = month(d.upload_date)\n" +
			"        ORDER BY views DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostViewedDocument\n" +
			"FROM document d\n" +
			"INNER JOIN `user` u ON d.user_id = u.user_id\n" +
			"GROUP BY year(d.upload_date), month(d.upload_date)\n" +
			"ORDER BY year(d.upload_date), month(d.upload_date);", nativeQuery = true)
	List<Object[]> listReportMonthCommon();

	@Query(value = "SELECT \n" +
			"    year(d.upload_date) AS year,\n" +
			"    QUARTER(d.upload_date) AS QUARTER,\n" +
			"    COUNT(*) AS totalDocuments,\n" +
			"    SUM(CASE WHEN d.favorite = true THEN 1 ELSE 0 END) AS favoriteDocuments,\n" +
			"    SUM(d.views) AS totalViews,\n" +
			"    (\n" +
			"        SELECT media_type\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND QUARTER(upload_date) = QUARTER(d.upload_date)\n" +
			"        GROUP BY media_type\n" +
			"        ORDER BY COUNT(*) DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS popularMediaType,\n" +
			"    COUNT(CASE WHEN d.status_id = 3 THEN 1 ELSE NULL END) AS completedDocuments,\n" +
			"    COUNT(CASE WHEN d.status_id = 1 THEN 1 ELSE NULL END) AS inProgressDocuments,\n" +
			"    COUNT(CASE WHEN d.status_id = 2 THEN 1 ELSE NULL END) AS deletedDocuments,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND QUARTER(upload_date) = QUARTER(d.upload_date)\n" +
			"        ORDER BY upload_date DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS latestDocument,\n" +
			"    (\n" +
			"        SELECT user_id\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND QUARTER(upload_date) = QUARTER(d.upload_date)\n" +
			"        GROUP BY user_id\n" +
			"        ORDER BY COUNT(*) DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostActiveUser,\n" +
			"    GROUP_CONCAT(DISTINCT u.name) AS userNames,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date) AND QUARTER(upload_date) = QUARTER(d.upload_date)\n" +
			"        ORDER BY views DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostViewedDocument\n" +
			"FROM document d\n" +
			"INNER JOIN `user` u ON d.user_id = u.user_id\n" +
			"GROUP BY year(d.upload_date), QUARTER(d.upload_date)\n" +
			"ORDER BY year(d.upload_date), QUARTER(d.upload_date);\n", nativeQuery = true)
	List<Object[]> listReportQuarterCommon();

	@Query(value = "select\n" +
			"\tu.name as UserName,\n" +
			"\tr.name as Rolename,\n" +
			"\tu.email as Email,\n" +
			"\tCOUNT(d.id) as TotalDocumentsUploaded,\n" +
			"\tIFNULL(SUM(d.views), 0) as TotalViews,\n" +
			"\tIFNULL(SUM(case when d.favorite = true then 1 else 0 end), 0) as TotalFavorites,\n" +
			"\tu.register_date as RegisterDate,\n" +
			"\tcase\n" +
			"\t\twhen u.status = true then 'Đang hoạt động'\n" +
			"\t\telse 'Ngừng hoạt động'\n" +
			"\tend as Status\n" +
			"from\n" +
			"\t`user` u\n" +
			"left join\n" +
			"    document d on\n" +
			"\tu.user_id = d.user_id\n" +
			"left join\n" +
			"    users_roles ur on\n" +
			"\tu.user_id = ur.user_id\n" +
			"left join\n" +
			"    `role` r on\n" +
			"\tur.role_id = r.id\n" +
			"where\n" +
			"\tr.name = 'ROLE_USER'\n" +
			"group by\n" +
			"\tu.user_id,\n" +
			"\tu.name,\n" +
			"\tu.email,\n" +
			"\tu.register_date,\n" +
			"\tu.status\n" +
			"order by TotalDocumentsUploaded desc;", nativeQuery = true)
	List<Object[]> listReportCustomerCommon();

}
