package org.example.tarea_gasolineras_eu.service;

import org.example.tarea_gasolineras_eu.model.GasPrice;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GasPriceService {

    private final String apiKey="MvPcQcgyxZeGVHBbb44q2Oxf968KLuc6zegsuvSh";
    private final WebClient webClient;

    public GasPriceService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<GasPrice> getPricesFromApi(String seriesId) {
        try {
            String response = webClient.get()
                    .uri("/v2/seriesid/{id}?api_key={key}", seriesId, apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject json = new JSONObject(response);
            JSONArray dataArray = json.getJSONObject("response").getJSONArray("data");

            List<GasPrice> prices = new ArrayList<>();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject entry = dataArray.getJSONObject(i);
                String date = entry.optString("period", "N/A");

                // Validar si el valor existe y es numérico
                if (!entry.isNull("value")) {
                    try {
                        double price = Double.parseDouble(entry.get("value").toString());
                        prices.add(new GasPrice(date, price));
                    } catch (NumberFormatException nfe) {
                        System.out.println("Valor no numérico en fecha " + date + ": " + entry.get("value"));
                    }
                } else {
                    System.out.println("Valor nulo en fecha " + date);
                }
            }

            return prices;

        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la API de EIA", e);
        }
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