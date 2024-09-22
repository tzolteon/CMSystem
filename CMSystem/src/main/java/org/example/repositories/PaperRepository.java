package org.example.repositories;

import org.example.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    boolean existsByTitle(String title);
    @Query(value = "SELECT p.* \n" +
            "FROM paper p \n" +
            "JOIN paper_authors pa ON p.id = pa.paper_id \n" +
            "JOIN users a ON pa.user_id = a.id \n" +
            "WHERE (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) \n" +
            "AND (:abstract IS NULL OR LOWER(p.abstract_text) LIKE LOWER(CONCAT('%', :abstract, '%'))) \n" +
            "AND (:authorName IS NULL OR LOWER(a.username) LIKE LOWER(CONCAT('%', :authorName, '%'))) \n" +
            "ORDER BY p.title ASC", nativeQuery = true)
    List<Paper> searchByCriteria(@Param("title") String title,
                                 @Param("authorName") String authorName,
                                 @Param("abstract") String abstractText);

    List<Paper> findByConferenceId(Long conferenceId);
}
