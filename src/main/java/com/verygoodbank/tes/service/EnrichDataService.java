package com.verygoodbank.tes.service;

import org.springframework.web.multipart.MultipartFile;

public interface EnrichDataService {
    String processTradeData(MultipartFile file);
}
