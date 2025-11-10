package com.sipcommb.envases.repository;

import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{
    
    @Query("SELECT c FROM Client c WHERE LOWER(c.name) = :name")
    Optional<Client> findByName(@Param("name") String name);

    @Override
    @NonNull Page<Client> findAll(@NonNull Pageable peageable);

    @Query("SELECT c FROM Client c WHERE c.is_active = :active")
    Page<Client> findAllActive(Pageable peageable, @Param("active") Boolean active);

    @Query("SELECT c FROM Client c WHERE c.name LIKE %:name% AND c.is_active = true")
    Page<Client> findLikeName(Pageable pageable, @Param("name") String name);

    @Query("SELECT c FROM Client c WHERE c.document = :document")
    Optional<Client> findByDocument(@Param("document") String document);
}
