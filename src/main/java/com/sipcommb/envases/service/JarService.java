package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarType;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.JarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JarService {

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private JarTypeRepository jarTypeRepository;

    // ============ JAR OPERATIONS ============

    
}
