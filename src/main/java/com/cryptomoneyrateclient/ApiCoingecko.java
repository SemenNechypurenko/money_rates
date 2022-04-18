package com.cryptomoneyrateclient;

import com.model.Coin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Data
@Service
public class ApiCoingecko {
    private final String API_BASE_URL = "https://api.coingecko.com/api/v3/";
    private final String MARKET = "https://api.coingecko.com/";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public Coin getRate(String title, String currency) throws IOException {
        String url = String.format(API_BASE_URL.concat("simple/price?ids=%s")
                .concat("&vs_currencies=%s"), title, currency);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode root = mapper.readTree(response.getBody());
        Double price = root.path(title).path(currency).asDouble();
        return new Coin(UUID.randomUUID().toString(), title, MARKET, price, currency, new Date());
    }
}
