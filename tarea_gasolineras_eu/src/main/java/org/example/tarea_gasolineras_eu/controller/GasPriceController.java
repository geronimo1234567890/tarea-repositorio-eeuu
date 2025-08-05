package org.example.tarea_gasolineras_eu.controller;

import org.example.tarea_gasolineras_eu.model.GasPrice;
import org.example.tarea_gasolineras_eu.model.GasPriceRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GasPriceController {

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("request", new GasPriceRequest());
        return "gas-price-form";
    }

    @PostMapping("/get-price")
    public String getPrice(@ModelAttribute GasPriceRequest request, Model model) {
        List<GasPrice> allPrices = fetchPricesFromApi(request); // Simula llamada a API

        List<GasPrice> filteredPrices = allPrices.stream()
                .filter(p -> request.getMinPrice() == null || p.getPrice() >= request.getMinPrice())
                .filter(p -> request.getMaxPrice() == null || p.getPrice() <= request.getMaxPrice())
                .collect(Collectors.toList());

        model.addAttribute("filteredPrices", filteredPrices);
        return "gas-price-result";
    }

    private List<GasPrice> fetchPricesFromApi(GasPriceRequest request) {
        // Simulación: deberías implementar la llamada real a la API
        return List.of(
                new GasPrice("2025-08-01", 3.25),
                new GasPrice("2025-08-02", 3.45),
                new GasPrice("2025-08-03", 3.75),
                new GasPrice("2025-08-04", 4.10)
        );
    }
}