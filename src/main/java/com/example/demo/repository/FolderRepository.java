package com.example.demo.repository;

import com.example.demo.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

  @Transactional
  void deleteByCategory_CategoryId(Long categoryId);

}
