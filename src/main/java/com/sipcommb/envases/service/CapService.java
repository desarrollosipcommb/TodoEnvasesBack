package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.CapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CapService {

    @Autowired
    private CapRepository capRepository;

}
