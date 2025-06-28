package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapRepository extends JpaRepository<Cap, Long> {
    

}
