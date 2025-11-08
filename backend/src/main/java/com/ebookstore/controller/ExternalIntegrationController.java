package com.ebookstore.controller;

import com.ebookstore.service.ExternalAuthorServiceClient;
import com.ebookstore.service.PriceCalculatorClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ExternalIntegrationController {
    private final ExternalAuthorServiceClient authorClient;
    private final PriceCalculatorClient priceClient;

    public ExternalIntegrationController(ExternalAuthorServiceClient authorClient,
                                         PriceCalculatorClient priceClient) {
        this.authorClient = authorClient;
        this.priceClient = priceClient;
    }

    @GetMapping("/author")
    public ResponseEntity<Map<String, Object>> queryAuthor(@RequestParam("title") String title) {
        Map<String, Object> resp = new HashMap<>();
        String author = authorClient.findAuthorByTitle(title);
        if (author != null) {
            resp.put("success", true);
            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("author", author);
            resp.put("data", data);
        } else {
            resp.put("success", false);
            resp.put("message", "未找到匹配书名");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/price")
    public ResponseEntity<Map<String, Object>> calcPrice(@RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        BigDecimal unitPrice = new BigDecimal(body.get("unitPrice").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());
        BigDecimal total = priceClient.calculate(unitPrice, quantity);
        resp.put("success", true);
        resp.put("total", total);
        return ResponseEntity.ok(resp);
    }
}


