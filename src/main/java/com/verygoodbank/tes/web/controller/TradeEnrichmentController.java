package com.verygoodbank.tes.web.controller;


import com.verygoodbank.tes.service.EnrichDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentController {
    private final EnrichDataService enrichDataService;
    private static final Logger logger = LogManager.getLogger(TradeEnrichmentController.class);

    public TradeEnrichmentController(EnrichDataService enrichDataService) {
        this.enrichDataService = enrichDataService;
    }

    @PostMapping(value = "/enrich", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> enrichData(@RequestPart("file") MultipartFile trade) {
        if (trade.isEmpty()) {
            logger.warn("Uploaded file is empty");
            return ResponseEntity.badRequest().body("Uploaded file is empty");
        }
        try {
            String enrichedData = enrichDataService.processTradeData(trade);
            logger.info("File successfully processed");
            return ResponseEntity.ok(enrichedData);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid file: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid file: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Failed to process uploaded file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process uploaded file: " + e.getMessage());
        }
    }
}
