package com.example.demo.repository;

import com.example.demo.dto.ChartDTO;
import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query(name = "getProductOrderCategories", nativeQuery = true)
	List<ChartDTO> getListProductOrderCategories();

	@Query(value = "SELECT c.category_name FROM document d " +
			"INNER JOIN folder f ON d.folder_id = f.folder_id " +
			"INNER JOIN categories c ON c.category_id = f.category_id " +
			"WHERE d.id = ?1", nativeQuery = true)
	String findCategoryNameByDocumentId(Long id);

}
