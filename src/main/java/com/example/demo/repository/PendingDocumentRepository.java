package com.example.demo.repository;

import com.example.demo.entity.PendingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PendingDocumentRepository extends JpaRepository<PendingDocument, Long> {

	@Query(value = "SELECT * FROM pending_document pd WHERE pd.status_id = 1", nativeQuery = true)
	List<PendingDocument> findAllByDocumentStatus();

	@Query(value = "SELECT count(id) from pending_document pd where pd.status_id = 1", nativeQuery = true)
	Long countAllByDocumentStatus();

	@Transactional
	void deleteByDocumentId(Long documentId);

}
