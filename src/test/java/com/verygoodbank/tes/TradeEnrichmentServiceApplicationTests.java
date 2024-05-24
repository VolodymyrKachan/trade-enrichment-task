package com.verygoodbank.tes;

import com.verygoodbank.tes.service.impl.EnrichDataServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class EnrichDataServiceImplTest {
    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void enrichData_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        String errorMessage = "Uploaded file is empty";

        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:8080/api/v1/enrich")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(errorMessage));
    }
}

