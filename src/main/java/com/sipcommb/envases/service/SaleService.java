package com.sipcommb.envases.service;


import com.sipcommb.envases.repository.SaleRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.UserRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.CapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



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
