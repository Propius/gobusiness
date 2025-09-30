package com.govtech.scrabble.repository;

import com.govtech.scrabble.entity.WordGenerationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for WordGenerationHistory entity operations.
 * Provides methods for tracking word generation usage patterns and performance metrics.
 */
@Repository
public interface WordGenerationHistoryRepository extends JpaRepository<WordGenerationHistory, Long> {

    /**
     * Find word generation history by generation mode.
     *
     * @param generationMode The generation mode (sampling or exhaustive)
     * @return List of word generation history entries for the specified mode
     */
    List<WordGenerationHistory> findByGenerationMode(String generationMode);

    /**
     * Find word generation history for a specific user session.
     *
     * @param userSession The user session identifier
     * @return List of word generation history entries for the specified session
     */
    List<WordGenerationHistory> findByUserSession(String userSession);

    /**
     * Find word generation history for a specific user session, ordered by date.
     *
     * @param userSession The user session identifier
     * @param pageable    Pagination information
     * @return Page of word generation history entries
     */
    Page<WordGenerationHistory> findByUserSessionOrderByGeneratedAtDesc(String userSession, Pageable pageable);

    /**
     * Find word generation history generated after a specific date.
     *
     * @param date The date to compare against
     * @return List of word generation history entries after the specified date
     */
    List<WordGenerationHistory> findByGeneratedAtAfter(LocalDateTime date);

    /**
     * Find word generation history within a date range.
     *
     * @param startDate Start of the date range
     * @param endDate   End of the date range
     * @return List of word generation history entries within the range
     */
    @Query("SELECT wgh FROM WordGenerationHistory wgh WHERE wgh.generatedAt BETWEEN :startDate AND :endDate")
    List<WordGenerationHistory> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Find slow word generation operations exceeding specified execution time.
     *
     * @param maxExecutionTime Maximum execution time threshold in milliseconds
     * @return List of slow word generation operations
     */
    @Query("SELECT wgh FROM WordGenerationHistory wgh WHERE wgh.executionTimeMs > :maxExecutionTime ORDER BY wgh.executionTimeMs DESC")
    List<WordGenerationHistory> findSlowOperations(@Param("maxExecutionTime") Integer maxExecutionTime);

    /**
     * Get average execution time for a specific generation mode.
     *
     * @param generationMode The generation mode (sampling or exhaustive)
     * @return Average execution time in milliseconds, or null if no data
     */
    @Query("SELECT AVG(wgh.executionTimeMs) FROM WordGenerationHistory wgh WHERE wgh.generationMode = :generationMode")
    Double getAverageExecutionTime(@Param("generationMode") String generationMode);

    /**
     * Get average results count for a specific generation mode.
     *
     * @param generationMode The generation mode (sampling or exhaustive)
     * @return Average results count, or null if no data
     */
    @Query("SELECT AVG(wgh.resultsCount) FROM WordGenerationHistory wgh WHERE wgh.generationMode = :generationMode")
    Double getAverageResultsCount(@Param("generationMode") String generationMode);

    /**
     * Count word generation operations by mode.
     *
     * @param generationMode The generation mode (sampling or exhaustive)
     * @return Count of operations for the specified mode
     */
    long countByGenerationMode(String generationMode);

    /**
     * Count word generation operations for a specific user session.
     *
     * @param userSession The user session identifier
     * @return Count of operations for the specified session
     */
    long countByUserSession(String userSession);

    /**
     * Get total results count for a specific generation mode.
     *
     * @param generationMode The generation mode (sampling or exhaustive)
     * @return Total number of words generated across all operations
     */
    @Query("SELECT SUM(wgh.resultsCount) FROM WordGenerationHistory wgh WHERE wgh.generationMode = :generationMode")
    Long getTotalResultsCountByMode(@Param("generationMode") String generationMode);

    /**
     * Delete word generation history older than specified date.
     *
     * @param date The date to compare against
     * @return Number of entries deleted
     */
    @Modifying
    @Query("DELETE FROM WordGenerationHistory wgh WHERE wgh.generatedAt < :date")
    int deleteOldHistory(@Param("date") LocalDateTime date);

    /**
     * Find most recent word generation operations for performance monitoring.
     *
     * @param pageable Pagination information (typically limited to recent N entries)
     * @return Page of recent word generation operations
     */
    Page<WordGenerationHistory> findAllByOrderByGeneratedAtDesc(Pageable pageable);

    /**
     * Get performance statistics for a specific mode within a date range.
     *
     * @param generationMode The generation mode
     * @param startDate      Start of the date range
     * @param endDate        End of the date range
     * @return List of history entries matching the criteria
     */
    @Query("SELECT wgh FROM WordGenerationHistory wgh WHERE wgh.generationMode = :generationMode " +
           "AND wgh.generatedAt BETWEEN :startDate AND :endDate ORDER BY wgh.generatedAt DESC")
    List<WordGenerationHistory> findPerformanceStatistics(@Param("generationMode") String generationMode,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
}