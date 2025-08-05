package org.example.tarea_gasolineras_eu.service;

import org.example.tarea_gasolineras_eu.model.GasPrice;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class GasPriceService {

    private final String apiKey = "MvPcQcgyxZeGVHBbb44q2Oxf968KLuc6zegsuvSh";
    private final WebClient webClient;

    public GasPriceService(WebClient.Builder builder) {
        // ⚙️ Aumentamos el límite de memoria a 10 MB
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();

        this.webClient = builder
                .baseUrl("https://api.eia.gov")
                .exchangeStrategies(strategies)
                .build();
    }
    public GasPrice getGasPriceBySeriesId(String seriesId) {
        try {
            String response = webClient.get()
                    .uri("/v2/seriesid/{id}?api_key={key}", seriesId, apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            assert response != null;
            JSONObject json = new JSONObject(response);
            JSONObject responseJson = json.getJSONObject("response");
            JSONArray dataArray = responseJson.getJSONArray("data");

            if (dataArray.isEmpty()) {
                throw new RuntimeException("No se encontraron datos en la respuesta.");
            }

            JSONObject latest = dataArray.getJSONObject(0);
            String date = latest.getString("period");
            double price = latest.getDouble("value");

            GasPrice gasPrice = new GasPrice();
            gasPrice.setDate(date);
            gasPrice.setPrice(price);

            return gasPrice;

        } catch (WebClientResponseException e) {
            System.err.println("Error HTTP: " + e.getStatusCode());
            System.err.println("Cuerpo del error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al consumir la API: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado al obtener el precio de gasolina", e);
        }
    }

    public GasPrice getNationalGasPrice() {

        try {
            String seriesId = "PET.EMM_EPMR_PTE_NUS_DPG.W";

            String response = webClient.get()
                    .uri("/v2/seriesid/{id}?api_key={key}", seriesId, apiKey)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                System.err.println("Error de la API: " + errorBody);
                                return Mono.error(new RuntimeException("Error en la API: " + errorBody));
                            }))
                    .bodyToMono(String.class)
                    .block();

            assert response != null;
            JSONObject json = new JSONObject(response);
            JSONObject responseJson = json.getJSONObject("response");
            JSONArray dataArray = responseJson.getJSONArray("data");

            if (dataArray.isEmpty()) {
                throw new RuntimeException("No se encontraron datos en la respuesta.");
            }

            JSONObject latest = dataArray.getJSONObject(0);
            String date = latest.getString("period");
            double price = latest.getDouble("value");

            GasPrice gasPrice = new GasPrice();
            gasPrice.setDate(date);
            gasPrice.setPrice(price);

            return gasPrice;

        } catch (WebClientResponseException e) {
            System.err.println("Error HTTP: " + e.getStatusCode());
            System.err.println("Cuerpo del error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al consumir la API: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw new RuntimeException("Error inesperado al obtener el precio de gasolina", e);
        }
    }
}