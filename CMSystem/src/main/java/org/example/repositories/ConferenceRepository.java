package org.example.repositories;

import org.example.entities.Conference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    boolean existsByTitle(String title);
    List<Conference> findByTitleContainingAndDescriptionContaining(String title, String description);
}
