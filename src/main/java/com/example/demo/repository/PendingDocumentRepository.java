package com.example.demo.repository;

import com.example.demo.entity.PendingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingDocumentRepository extends JpaRepository<PendingDocument, Long> {

}
