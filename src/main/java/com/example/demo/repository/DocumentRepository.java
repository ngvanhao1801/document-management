package com.example.demo.repository;

import com.example.demo.dto.ChartDTO;
import com.example.demo.entity.Document;
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
	List<Document> listDocumentByFolder(Long folderId);

	// Top 10 product by category
	@Query(value = "SELECT * FROM document AS b WHERE b.status_id = 3 and b.folder_id = ?;", nativeQuery = true)
	List<Document> listDocumentByFolder10(Long folderId);

	// List product new
	@Query(value = "SELECT * FROM document d where d.status_id = 3 ORDER BY d.upload_date DESC limit 10;",
			nativeQuery = true)
	List<Document> listDocumentNew();

	// Search Product
	@Query(value = "SELECT * FROM document d WHERE d.status_id = 3 and d.document_name LIKE %?1%", nativeQuery = true)
	List<Document> searchDocument(String documentName);

	@Query(value = "SELECT f.folder_id, f.folder_name, " +
			"COUNT(*) AS SoLuong " +
			"FROM document d " +
			"JOIN folder f ON d.folder_id = f.folder_id " +
			"WHERE d.status_id = 3 " +
			"GROUP BY f.folder_name;", nativeQuery = true)
	List<Object[]> listFolderByDocumentName();

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
			"    (\n" +
			"        SELECT COUNT(DISTINCT f.document_id)\n" +
			"        FROM favorites f\n" +
			"        INNER JOIN document d2 ON f.document_id = d2.id\n" +
			"        WHERE year(d2.upload_date) = year(d.upload_date)\n" +
			"    ) AS favoriteDocuments,\n" +
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
			"        SELECT COUNT(DISTINCT user_id)\n" +
			"        FROM document\n" +
			"        WHERE YEAR(upload_date) = YEAR(d.upload_date)\n" +
			"    ) AS totalUsersUploaded," +
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
			"    ) AS mostViewedDocument,\n" +
			"    (SELECT document_name\n" +
			"    FROM document\n" +
			"    WHERE year(upload_date) = year(d.upload_date)\n" +
			"    ORDER BY favorites DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostFavouriteDocument\n" +
			"FROM document d\n" +
			"GROUP BY year(d.upload_date)\n" +
			"ORDER BY year(d.upload_date);", nativeQuery = true)
	List<Object[]> listReportYearCommon();

	@Query(value = "SELECT \n" +
			"    year(d.upload_date) AS year,\n" +
			"    month(d.upload_date) AS month,\n" +
			"    COUNT(*) AS totalDocuments,\n" +
			"    (\n" +
			"        SELECT COUNT(DISTINCT f.document_id)\n" +
			"        FROM favorites f\n" +
			"        INNER JOIN document d2 ON f.document_id = d2.id\n" +
			"        WHERE year(d2.upload_date) = year(d.upload_date)\n" +
			"        AND month(upload_date) = month(d.upload_date)\n" +
			"    ) AS favoriteDocuments,\n" +
			"    SUM(d.views) AS totalViews,\n" +
			"    (\n" +
			"        SELECT media_type\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        AND month(upload_date) = month(d.upload_date)\n" +
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
			"        AND month(upload_date) = month(d.upload_date)\n" +
			"        ORDER BY upload_date DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS latestDocument,\n" +
			"		(\n" +
			"        SELECT COUNT(DISTINCT user_id)\n" +
			"        FROM document\n" +
			"        WHERE YEAR(upload_date) = YEAR(d.upload_date)\n" +
			"    ) AS totalUsersUploaded," +
			"    (\n" +
			"        SELECT GROUP_CONCAT(DISTINCT u.name)\n" +
			"        FROM document doc\n" +
			"        INNER JOIN `user` u ON doc.user_id = u.user_id\n" +
			"        WHERE year(doc.upload_date) = year(d.upload_date)\n" +
			"        AND month(upload_date) = month(d.upload_date)\n" +
			"    ) AS userNames,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        AND month(upload_date) = month(d.upload_date)\n" +
			"        ORDER BY views DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostViewedDocument,\n" +
			"    (SELECT document_name\n" +
			"    FROM document\n" +
			"    WHERE year(upload_date) = year(d.upload_date)\n" +
			"    AND month(upload_date) = month(d.upload_date)\n" +
			"    ORDER BY favorites DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostFavouriteDocument\n" +
			"FROM document d\n" +
			"GROUP BY year(d.upload_date), month(d.upload_date)\n" +
			"ORDER BY year(d.upload_date), month(d.upload_date);", nativeQuery = true)
	List<Object[]> listReportMonthCommon();

	@Query(value = "SELECT \n" +
			"    year(d.upload_date) AS year,\n" +
			"    quarter(d.upload_date) AS quarter,\n" +
			"    COUNT(*) AS totalDocuments,\n" +
			"    (\n" +
			"        SELECT COUNT(DISTINCT f.document_id)\n" +
			"        FROM favorites f\n" +
			"        INNER JOIN document d2 ON f.document_id = d2.id\n" +
			"        WHERE year(d2.upload_date) = year(d.upload_date)\n" +
			"        AND quarter(upload_date) = quarter(d.upload_date)\n" +
			"    ) AS favoriteDocuments,\n" +
			"    SUM(d.views) AS totalViews,\n" +
			"    (\n" +
			"        SELECT media_type\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        AND quarter(upload_date) = quarter(d.upload_date)\n" +
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
			"        AND quarter(upload_date) = quarter(d.upload_date)\n" +
			"        ORDER BY upload_date DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS latestDocument,\n" +
			"    (\n" +
			"        SELECT COUNT(DISTINCT user_id)\n" +
			"        FROM document\n" +
			"        WHERE YEAR(upload_date) = YEAR(d.upload_date)\n" +
			"    ) AS totalUsersUploaded," +
			"    (\n" +
			"        SELECT GROUP_CONCAT(DISTINCT u.name)\n" +
			"        FROM document doc\n" +
			"        INNER JOIN `user` u ON doc.user_id = u.user_id\n" +
			"        WHERE year(doc.upload_date) = year(d.upload_date)\n" +
			"        AND quarter(upload_date) = quarter(d.upload_date)\n" +
			"    ) AS userNames,\n" +
			"    (\n" +
			"        SELECT document_name\n" +
			"        FROM document\n" +
			"        WHERE year(upload_date) = year(d.upload_date)\n" +
			"        AND quarter(upload_date) = quarter(d.upload_date)\n" +
			"        ORDER BY views DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostViewedDocument,\n" +
			"    (SELECT document_name\n" +
			"    FROM document\n" +
			"    WHERE year(upload_date) = year(d.upload_date)\n" +
			"    AND quarter(upload_date) = quarter(d.upload_date)\n" +
			"    ORDER BY favorites DESC\n" +
			"        LIMIT 1\n" +
			"    ) AS mostFavouriteDocument\n" +
			"FROM document d\n" +
			"GROUP BY year(d.upload_date), quarter(d.upload_date)\n" +
			"ORDER BY year(d.upload_date), quarter(d.upload_date);\n", nativeQuery = true)
	List<Object[]> listReportQuarterCommon();

	@Query(value = "SELECT\n" +
			"    u.name AS UserName,\n" +
			"    r.name AS RoleName,\n" +
			"    u.email AS Email,\n" +
			"    COUNT(d.id) AS TotalDocumentsUploaded,\n" +
			"    IFNULL(SUM(d.views), 0) AS TotalViews,\n" +
			"    IFNULL(\n" +
			"        (SELECT COUNT(DISTINCT f.document_id)\n" +
			"         FROM favorites f\n" +
			"         INNER JOIN document d2 ON f.document_id = d2.id\n" +
			"         WHERE d2.user_id = u.user_id), 0) AS TotalFavorites,\n" +
			"    u.register_date AS RegisterDate,\n" +
			"    CASE\n" +
			"        WHEN u.status = true THEN 'Đang hoạt động'\n" +
			"        ELSE 'Ngừng hoạt động'\n" +
			"    END AS Status\n" +
			"FROM\n" +
			"    `user` u\n" +
			"LEFT JOIN\n" +
			"    document d ON u.user_id = d.user_id\n" +
			"LEFT JOIN\n" +
			"    users_roles ur ON u.user_id = ur.user_id\n" +
			"LEFT JOIN\n" +
			"    `role` r ON ur.role_id = r.id\n" +
			"WHERE\n" +
			"    r.name = 'ROLE_USER'\n" +
			"GROUP BY\n" +
			"    u.user_id,\n" +
			"    u.name,\n" +
			"    u.email,\n" +
			"    u.register_date,\n" +
			"    u.status\n" +
			"ORDER BY\n" +
			"    RegisterDate DESC;", nativeQuery = true)
	List<Object[]> listReportCustomerCommon();

}
