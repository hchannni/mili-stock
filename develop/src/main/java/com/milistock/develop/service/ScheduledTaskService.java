package com.milistock.develop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void updateProductStatus() {
        productService.updateProductStatus();
    }
}
