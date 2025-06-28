package com.cheikh.commun.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SwaggerAggregatorController {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerAggregatorController.class);

    private static final String CUSTOMER_SERVICE_URL = "/api/v1/customers/v3/api-docs";
    private static final String ORDER_SERVICE_URL = "/api/v1/orders/v3/api-docs";
    private static final String PRODUCT_SERVICE_URL = "/api/v1/products/v3/api-docs";
    private static final String PAYMENT_SERVICE_URL = "/api/v1/payments/v3/api-docs";

    private static final String CUSTOMER_SERVICE = "Customer Service";
    private static final String ORDER_SERVICE = "Order Service";
    private static final String PRODUCT_SERVICE = "Product Service";
    private static final String PAYMENT_SERVICE = "Payment Service";

    @Operation(summary = "Get Swagger UI Configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved configuration")
    })
    @GetMapping("/swagger-resources/configuration/ui")
    public ResponseEntity<Map<String, Object>> getSwaggerUIConfig() {
        Map<String, Object> config = Map.of("url", "/swagger-resources");
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Get Swagger Service Resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved service resources")
    })
    @GetMapping("/swagger-resources")
    public ResponseEntity<List<Map<String, String>>> getSwaggerResources() {
        List<Map<String, String>> resources = List.of(
                createSwaggerServiceResource(CUSTOMER_SERVICE, CUSTOMER_SERVICE_URL),
                createSwaggerServiceResource(ORDER_SERVICE, ORDER_SERVICE_URL),
                createSwaggerServiceResource(PRODUCT_SERVICE, PRODUCT_SERVICE_URL),
                createSwaggerServiceResource(PAYMENT_SERVICE, PAYMENT_SERVICE_URL)
        );
        logger.info("Swagger resources: {}", resources);
        return ResponseEntity.ok(resources);
    }

    private Map<String, String> createSwaggerServiceResource(String name, String url) {
        return Map.of(
                "name", name,
                "url", url,
                "swaggerVersion", "3.0",
                "location", url
        );
    }
}
