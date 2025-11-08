package com.ebookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExternalAuthorServiceClient {
    private final RestTemplate restTemplate;

    public ExternalAuthorServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String findAuthorByTitle(String title) {
        String url = "http://author-service/author/lookup?title=" + title;
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = restTemplate.getForObject(url, Map.class);
        if (resp != null && Boolean.TRUE.equals(resp.get("success"))) {
            Object data = resp.get("data");
            if (data instanceof Map) {
                Object author = ((Map<?, ?>) data).get("author");
                return author != null ? author.toString() : null;
            }
        }
        return null;
    }
}


