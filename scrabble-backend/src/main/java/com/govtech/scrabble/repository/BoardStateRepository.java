package com.govtech.scrabble.repository;

import com.govtech.scrabble.entity.BoardState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BoardState entity operations.
 * Provides methods for storing and retrieving board states for the board analyzer feature.
 */
@Repository
public interface BoardStateRepository extends JpaRepository<BoardState, Long> {

    /**
     * Find all board states for a specific user session.
     *
     * @param userSession The user session identifier
     * @return List of board states for the specified session
     */
    List<BoardState> findByUserSession(String userSession);

    /**
     * Find all board states for a specific user session, ordered by last modified date.
     *
     * @param userSession The user session identifier
     * @param pageable    Pagination information
     * @return Page of board states for the specified session
     */
    Page<BoardState> findByUserSessionOrderByLastModifiedDesc(String userSession, Pageable pageable);

    /**
     * Find the most recent board state for a specific user session.
     *
     * @param userSession The user session identifier
     * @return Optional containing the most recent board state if found
     */
    Optional<BoardState> findFirstByUserSessionOrderByLastModifiedDesc(String userSession);

    /**
     * Find board states created after a specific date.
     *
     * @param date The date to compare against
     * @return List of board states created after the specified date
     */
    List<BoardState> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find board states with analysis score greater than or equal to specified value.
     *
     * @param minScore Minimum analysis score threshold
     * @return List of high-scoring board states
     */
    @Query("SELECT bs FROM BoardState bs WHERE bs.analysisScore >= :minScore ORDER BY bs.analysisScore DESC")
    List<BoardState> findHighScoringBoards(@Param("minScore") Integer minScore);

    /**
     * Find board states modified within a specific time range.
     *
     * @param startDate Start of the time range
     * @param endDate   End of the time range
     * @return List of board states modified within the specified range
     */
    @Query("SELECT bs FROM BoardState bs WHERE bs.lastModified BETWEEN :startDate AND :endDate")
    List<BoardState> findByModifiedBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Delete board states older than specified date (for cleanup).
     *
     * @param date The date to compare against
     * @return Number of board states deleted
     */
    @Modifying
    @Query("DELETE FROM BoardState bs WHERE bs.createdAt < :date")
    int deleteOldBoardStates(@Param("date") LocalDateTime date);

    /**
     * Delete all board states for a specific user session.
     *
     * @param userSession The user session identifier
     * @return Number of board states deleted
     */
    @Modifying
    int deleteByUserSession(String userSession);

    /**
     * Count board states for a specific user session.
     *
     * @param userSession The user session identifier
     * @return Count of board states for the specified session
     */
    long countByUserSession(String userSession);

    /**
     * Get average analysis score for a specific user session.
     *
     * @param userSession The user session identifier
     * @return Average analysis score, or null if no board states exist
     */
    @Query("SELECT AVG(bs.analysisScore) FROM BoardState bs WHERE bs.userSession = :userSession")
    Double getAverageScoreByUserSession(@Param("userSession") String userSession);
}