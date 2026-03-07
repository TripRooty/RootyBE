package com.github.triprooty.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${google.places.api-key:}")
    private String apiKey;

    public PlaceSearchResult searchText(String query) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("google.places.api-key is not configured.");
        }

        RestClient client = restClientBuilder.baseUrl("https://maps.googleapis.com").build();

        String uri = UriComponentsBuilder
                .fromPath("/maps/api/place/textsearch/json")
                .queryParam("query", query)
                .queryParam("language", "ko")
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        String responseJson = client.get()
                .uri(uri)
                .retrieve()
                .body(String.class);

        return new PlaceSearchResult(responseJson, extractCandidates(responseJson));
    }

    private List<PlaceCandidate> extractCandidates(String responseJson) {
        List<PlaceCandidate> candidates = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode results = root.path("results");
            if (!results.isArray()) {
                return candidates;
            }

            for (JsonNode node : results) {
                String placeId = node.path("place_id").asText(null);
                if (placeId == null || placeId.isBlank()) {
                    continue;
                }

                String name = node.path("name").asText("");
                String address = node.path("formatted_address").asText(null);

                JsonNode locationNode = node.path("geometry").path("location");
                BigDecimal lat = locationNode.has("lat") ? locationNode.path("lat").decimalValue() : null;
                BigDecimal lng = locationNode.has("lng") ? locationNode.path("lng").decimalValue() : null;

                candidates.add(new PlaceCandidate(placeId, name, address, lat, lng));
            }
        } catch (Exception ignored) {
            return List.of();
        }

        return candidates;
    }

    public record PlaceSearchResult(String responseJson, List<PlaceCandidate> candidates) {
    }

    public record PlaceCandidate(
            String googlePlaceId,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }
}
