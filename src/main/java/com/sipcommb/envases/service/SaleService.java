package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.repository.SaleRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.UserRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.CapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SaleService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private SaleItemRepository saleItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JarRepository jarRepository;
    
    @Autowired
    private CapRepository capRepository;
    
    
}
