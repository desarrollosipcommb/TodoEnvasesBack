package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CapRepository extends JpaRepository<Cap, Long> {

        boolean existsByName(String name);

        @Query("SELECT c FROM Cap c WHERE c.name = :name and c.jarType.diameter = :diameter AND c.isActive = 1")
        Optional<Cap> findByNameAndDiameter(@Param("name") String name,
                        @Param("diameter") String diameter);

        @Query("SELECT c FROM Cap c WHERE c.name = :name and c.jarType.diameter = :diameter")
        Optional<Cap> findByNameAndDiameterIncludingInactive(@Param("name") String name,
                        @Param("diameter") String diameter);

        @Query("SELECT c FROM Cap c WHERE c.name LIKE %:name% AND c.jarType.diameter = :diameter AND c.isActive = 1")
        Optional<List<Cap>> getFromNameAndDiameter(String name, String diameter);

        @Query("SELECT c FROM Cap c WHERE c.jarType.diameter = :diameter AND c.isActive = 1")
        Optional<Page<Cap>> getFromCapDiameter(@Param("diameter") String diameter, Pageable pageable);

        @Query("SELECT c FROM Cap c WHERE c.jarType.diameter = :diameter AND c.isActive = 1")
        Optional<List<Cap>> getFromCapDiameter(@Param("diameter") String diameter);

        @Query("SELECT c FROM Cap c WHERE c.name LIKE %:name% AND c.isActive = 1")
        Optional<Page<Cap>> getFromNameLike(@Param("name") String name, Pageable pageable);

        @Query("SELECT c FROM Cap c WHERE c.isActive = 1")
        List<Cap> findAllByIsActive();

        @Query("SELECT c FROM Cap c WHERE c.isActive = 0")
        List<Cap> findAllByIsActiveFalse();

        Page<Cap> findAll(Pageable pageable);

        Page<Cap> findAllByIsActiveTrue(Pageable pageable);

        Page<Cap> findAllByIsActiveFalse(Pageable pageable);

        @Query("SELECT c FROM Cap c " +
                        "JOIN c.jarType jt " +
                        "WHERE (:name IS NULL OR c.name LIKE %:name%) " +
                        "AND (:diameter IS NULL OR jt.diameter LIKE %:diameter%) ")
        Page<Cap> getFromNameLikeAndDiameter(@Param("name") String name,
                        @Param("diameter") String diameter, Pageable pageable);

        @Query("SELECT c FROM Cap c " +
                        "JOIN c.jarType jt " +
                        "WHERE (:name IS NULL OR c.name LIKE %:name%) " +
                        "AND (:diameter IS NULL OR jt.diameter LIKE %:diameter%) " +
                        "AND c.isActive = 1")
        Page<Cap> getFromNameLikeAndDiameterActive(@Param("name") String name,
                        @Param("diameter") String diameter, Pageable pageable);

}
