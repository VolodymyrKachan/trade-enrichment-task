package com.verygoodbank.tes;

import com.verygoodbank.tes.service.impl.EnrichDataServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class EnrichDataServiceImplTest {

    @Test
    void processTradeData_WithValidData_ShouldReturnEnrichedData() {

        String csvContent = "data, product_name, currency, price\n" +
                "20240101,1,USD,100.00\n" +
                "20240102,2,GBP,200.00\n";
        MockMultipartFile file = new MockMultipartFile("trade", "trade.csv", "text/csv", csvContent.getBytes());
        EnrichDataServiceImpl enrichDataService = new EnrichDataServiceImpl();

        String enrichedData = enrichDataService.processTradeData(file);

        assertNotNull(enrichedData);
        assertTrue(enrichedData.contains("data, product_name, currency, price"));
        assertTrue(enrichedData.contains("20240101,Treasury Bills Domestic,USD,100.00"));
        assertTrue(enrichedData.contains("20240102,Corporate Bonds Domestic,GBP,200.00"));
    }

    @Test
    void processTradeData_WithValidData_productIdWithoutProductFile_ShouldReturnEnrichedData() {

        String csvContent = "data, product_id, currency, price\n" +
                "20240101,1,USD,100.00\n" +
                "20240102,2,GBP,200.00\n" +
                "20240102,22,UAH,1200.00\n";
        MockMultipartFile file = new MockMultipartFile("trade", "trade.csv", "text/csv", csvContent.getBytes());
        EnrichDataServiceImpl enrichDataService = new EnrichDataServiceImpl();

        String enrichedData = enrichDataService.processTradeData(file);

        assertNotNull(enrichedData);
        assertTrue(enrichedData.contains("data, product_name, currency, price"));
        assertTrue(enrichedData.contains("20240101,Treasury Bills Domestic,USD,100.00"));
        assertTrue(enrichedData.contains("20240102,Corporate Bonds Domestic,GBP,200.00"));
        assertTrue(enrichedData.contains("20240102,Missing Product Name,UAH,1200.00"));
    }

    @Test
    void processTradeData_WithEmptyFile_ShouldThrowIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", new byte[0]);
        EnrichDataServiceImpl enrichDataService = new EnrichDataServiceImpl();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            enrichDataService.processTradeData(file);
        });
        assertEquals("Uploaded file is empty", exception.getMessage());
    }

    @Test
    void processTradeData_WithInvalidDateFormat_ShouldIgnoreAndLogError() {
        String csvContent = "data, product_name, currency, price\n" +
                "2024010101,1,USD,100.00\n" +
                "20240102,2,GBP,200.00\n";
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", csvContent.getBytes());
        EnrichDataServiceImpl enrichDataService = new EnrichDataServiceImpl();

        String enrichedData = enrichDataService.processTradeData(file);

        assertNotNull(enrichedData);
        assertFalse(enrichedData.contains("2024010101"));
        assertTrue(enrichedData.contains("20240102,Corporate Bonds Domestic,GBP,200.00"));
        assertFalse(enrichedData.contains("2024010101,Treasury Bills Domestic,USD,100.00"));
    }
}

