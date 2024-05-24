package com.verygoodbank.tes.service.impl;

import com.verygoodbank.tes.service.EnrichDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnrichDataServiceImpl implements EnrichDataService {
    private static final Logger logger = LogManager.getLogger(EnrichDataServiceImpl.class);
    private static final Map<Long, String> productMap = new HashMap<>();
    private static final String FILE_TITLE = "data, product_name, currency, price";
    private static final String DATE_FORMATTER = "yyyyMMdd";

    static {
        try (BufferedReader productReader = new BufferedReader(new FileReader("src/main/resources/product.csv"))) {
            String productLine;
            productReader.readLine();
            while ((productLine = productReader.readLine()) != null) {
                String[] productParts = productLine.split(",");
                productMap.put(Long.parseLong(productParts[0]), productParts[1]);
            }
        } catch (FileNotFoundException e) {
            String errorMessage = "Product.csv file not found: " + e.getMessage();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage, e);
        } catch (IOException e) {
            String errorMessage = "Error reading product.csv file: " + e.getMessage();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage, e);
        }
    }

    public String processTradeData(MultipartFile file) {
        if (file.isEmpty()) {
            String errorMessage = "Uploaded file is empty";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<String> enrichedData = new ArrayList<>();
            enrichedData.add(FILE_TITLE);
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    continue;
                }
                LocalDate localDate = null;
                String date;
                try {
                    localDate = LocalDate.parse(parts[0], DateTimeFormatter.ofPattern(DATE_FORMATTER));
                    date = localDate.format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
                } catch (DateTimeParseException e) {
                    String errorMessage = "Invalid date format: " + e.getMessage();
                    logger.error(errorMessage, localDate);
                    continue;
                }

                Long productId = Long.parseLong(parts[1]);
                String productName = productMap.getOrDefault(productId, "Missing Product Name");

                String currency = parts[2];
                BigDecimal price = new BigDecimal(parts[3]);

                StringBuilder enrichedLineBuilder = new StringBuilder();
                enrichedLineBuilder.append(date).append(",").append(productName).append(",").append(currency).append(",").append(price);
                String enrichedLine = enrichedLineBuilder.toString();
                enrichedData.add(enrichedLine);
            }

            return String.join("\n", enrichedData);
        } catch (IOException e) {
            String errorMessage = "Error reading uploaded file: " + e.getMessage();
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage, e);
        }
    }
}
