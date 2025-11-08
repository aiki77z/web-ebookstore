package com.ebookstore.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PriceCalculatorClient {
    private final RestTemplate restTemplate;

    public PriceCalculatorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal calculate(BigDecimal unitPrice, int quantity) {
        String url = "http://price-function-service/price";
        Map<String, Object> body = new HashMap<>();
        body.put("unitPrice", unitPrice);
        body.put("quantity", quantity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = restTemplate.postForObject(url, req, Map.class);
        if (resp != null && Boolean.TRUE.equals(resp.get("success"))) {
            Object total = resp.get("total");
            if (total != null) {
                return new BigDecimal(total.toString());
            }
        }
        // 失败回退到本地计算
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}


