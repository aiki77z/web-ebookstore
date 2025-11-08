package com.ebookstore.price.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class PriceCalculatorFunction {

    public static class Input {
        private final BigDecimal unitPrice;
        private final Integer quantity;

        @JsonCreator
        public Input(@JsonProperty("unitPrice") BigDecimal unitPrice,
                     @JsonProperty("quantity") Integer quantity) {
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public Integer getQuantity() { return quantity; }
    }

    @Bean(name = "price")
    public Function<Input, Map<String, Object>> price() {
        return in -> {
            Map<String, Object> result = new HashMap<>();
            if (in == null || in.getUnitPrice() == null || in.getQuantity() == null) {
                result.put("success", false);
                result.put("message", "缺少必要参数");
                return result;
            }
            BigDecimal total = in.getUnitPrice().multiply(BigDecimal.valueOf(in.getQuantity()));
            result.put("success", true);
            result.put("total", total);
            return result;
        };
    }
}


