package com.surgical.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/health")
public class HealthCheckController{

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @Autowired
    JdbcTemplate template;

    @GetMapping("/check")
    public Integer init(HttpServletRequest request, HttpServletResponse response){
        logger.info("controller test");
        List<Object> results = template.query("select 1 from dual", new SingleColumnRowMapper<>());
        return results.size();
    }
}
