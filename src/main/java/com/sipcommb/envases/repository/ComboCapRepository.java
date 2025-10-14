package com.sipcommb.envases.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sipcommb.envases.entity.ComboCap;


@Repository
public interface ComboCapRepository extends JpaRepository<ComboCap, Integer> {


    Optional<ComboCap> findByComboIdAndCapId(Long comboId, Long capId);
}
