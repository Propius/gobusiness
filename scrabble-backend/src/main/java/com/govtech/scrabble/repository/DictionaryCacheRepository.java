package com.govtech.scrabble.repository;

import com.govtech.scrabble.entity.DictionaryCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DictionaryCache entity operations.
 * Provides methods for caching and retrieving dictionary validation results.
 */
@Repository
public interface DictionaryCacheRepository extends JpaRepository<DictionaryCache, String> {

    /**
     * Find all cached entries by dictionary source.
     *
     * @param dictionarySource The dictionary source to filter by
     * @return List of cached entries from the specified source
     */
    List<DictionaryCache> findByDictionarySource(String dictionarySource);

    /**
     * Find all valid words cached.
     *
     * @param isValid True to find valid words, false for invalid words
     * @return List of cached entries matching the validity status
     */
    List<DictionaryCache> findByIsValid(Boolean isValid);

    /**
     * Find cached entries accessed after a specific date.
     *
     * @param date The date to compare against
     * @return List of cached entries accessed after the specified date
     */
    List<DictionaryCache> findByLastAccessedAfter(LocalDateTime date);

    /**
     * Find cached entries with access count greater than specified value.
     *
     * @param minAccessCount Minimum access count threshold
     * @return List of frequently accessed cached entries
     */
    @Query("SELECT dc FROM DictionaryCache dc WHERE dc.accessCount >= :minAccessCount ORDER BY dc.accessCount DESC")
    List<DictionaryCache> findMostAccessed(@Param("minAccessCount") Integer minAccessCount);

    /**
     * Find cached entries older than specified date (for cache cleanup).
     *
     * @param date The date to compare against
     * @return List of stale cached entries
     */
    List<DictionaryCache> findByCachedAtBefore(LocalDateTime date);

    /**
     * Delete cached entries older than specified date.
     *
     * @param date The date to compare against
     * @return Number of entries deleted
     */
    @Modifying
    @Query("DELETE FROM DictionaryCache dc WHERE dc.cachedAt < :date")
    int deleteOldEntries(@Param("date") LocalDateTime date);

    /**
     * Get total count of cached entries by validity status.
     *
     * @param isValid True for valid words count, false for invalid words count
     * @return Count of cached entries matching the validity status
     */
    long countByIsValid(Boolean isValid);

    /**
     * Find word in cache and update access tracking.
     * This is a convenience method that combines lookup with access tracking.
     *
     * @param word The word to look up
     * @return Optional containing the cached entry if found
     */
    default Optional<DictionaryCache> findAndRecordAccess(String word) {
        Optional<DictionaryCache> cached = findById(word);
        cached.ifPresent(entry -> {
            entry.recordAccess();
            save(entry);
        });
        return cached;
    }
}