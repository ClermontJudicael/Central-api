package com.api.central.controller;

import com.api.central.service.SynchronizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/syncrhonization")
@AllArgsConstructor
public class SynchronizationController {

    private final SynchronizationService synchronizationService;

    @PostMapping
    public ResponseEntity<String> synchronize() throws SQLException {
        synchronizationService.synchronizeAll();
        return ResponseEntity.ok("Data synchronized successfully.");
    }
}

