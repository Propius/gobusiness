package com.govtech.scrabble.repository;

import com.govtech.scrabble.entity.Score;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    
    @Query("SELECT s FROM Score s ORDER BY s.points DESC, s.createdAt DESC")
    List<Score> findTopScores(Pageable pageable);
}