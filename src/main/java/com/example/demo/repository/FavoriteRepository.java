package com.example.demo.repository;

import com.example.demo.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	// favorite
	@Query(value = "SELECT f.* FROM favorites f " +
			"inner join document d on f.document_id = d.id " +
			"where d.status_id = 3 " +
			"and f.document_id  = ? " +
			"and f.user_id = ?;", nativeQuery = true)
	public Favorite selectSaves(Long documentId, Long userId);

	@Query(value = "select f.* from favorites f " +
			"inner join document d on f.document_id = d.id " +
			"where f.user_id = ? and d.status_id = 3;", nativeQuery = true)
	public List<Favorite> selectAllSaves(Long userId);

	@Query(value = "select count(f.favorite_id) from favorites f\n" +
			"inner join document d on f.document_id = d.id  \n" +
			"where f.user_id = ? and d.status_id = 3;", nativeQuery = true)
	public Integer selectCountSave(Long userId);

}
