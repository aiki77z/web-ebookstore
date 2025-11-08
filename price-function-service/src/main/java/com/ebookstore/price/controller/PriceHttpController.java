package com.ebookstore.price.controller;

import com.ebookstore.price.function.PriceCalculatorFunction;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/price")
public class PriceHttpController {

    private final Function<PriceCalculatorFunction.Input, Map<String, Object>> priceFunction;

    public PriceHttpController(@Qualifier("price") Function<PriceCalculatorFunction.Input, Map<String, Object>> priceFunction) {
        this.priceFunction = priceFunction;
    }

    // 便于浏览器直接验证的 GET 接口：/price/calc?unitPrice=59.9&quantity=3
    @GetMapping("/calc")
    public Map<String, Object> calc(@RequestParam("unitPrice") BigDecimal unitPrice,
                                    @RequestParam("quantity") Integer quantity) {
        return priceFunction.apply(new PriceCalculatorFunction.Input(unitPrice, quantity));
    }

    // 提供传统 REST 方式的 POST 验证：/price/compute 以避免函数式路由造成的困惑
    @PostMapping(path = "/compute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> compute(@RequestBody PriceCalculatorFunction.Input input) {
        return priceFunction.apply(input);
    }
}


