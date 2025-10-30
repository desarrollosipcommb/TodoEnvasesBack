package com.sipcommb.envases.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Bodega;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    @Query("SELECT b FROM Bodega b WHERE b.name = :name")
    Optional<Bodega> findByName(@Param("name") String name);

    @Query("SELECT b.name FROM Bodega b")
    List<String> findAllBodegasNames();

    @Query("SELECT b.name FROM Bodega b WHERE b.name LIKE %:nameFilter%")
    List<String> findBodegasByNameFilter(@Param("nameFilter") String nameFilter);

    @Query("SELECT b.name FROM Bodega b")
    Page<String> findAllBodegasNames(Pageable pageable);

}
