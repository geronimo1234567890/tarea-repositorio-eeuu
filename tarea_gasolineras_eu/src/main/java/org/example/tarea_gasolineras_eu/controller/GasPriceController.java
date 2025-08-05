package org.example.tarea_gasolineras_eu.controller;

import org.example.tarea_gasolineras_eu.model.GasPrice;
import org.example.tarea_gasolineras_eu.model.GasPriceRequest;
import org.example.tarea_gasolineras_eu.model.SeriesIdBuilder;
import org.example.tarea_gasolineras_eu.service.GasPriceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GasPriceController {

    private final GasPriceService gasPriceService;

    public GasPriceController(GasPriceService gasPriceService) {
        this.gasPriceService = gasPriceService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("request", new GasPriceRequest());
        return "gas-price-form";
    }

    @PostMapping("/get-price")
    public String getPrice(@ModelAttribute GasPriceRequest request, Model model) {
        // 1. Construir el seriesId dinámicamente
        String seriesId = new SeriesIdBuilder()
                .setProduct(request.getProduct())
                .setArea(request.getArea())
                .setFrequency(request.getFrequency())
                .build();

        // 2. Llamar a la API
        List<GasPrice> allPrices = gasPriceService.getPricesFromApi(seriesId);

        // 3. Aplicar filtros de precio
        List<GasPrice> filteredPrices = allPrices.stream()
                .filter(p -> request.getMinPrice() == null || p.getPrice() >= request.getMinPrice())
                .filter(p -> request.getMaxPrice() == null || p.getPrice() <= request.getMaxPrice())
                .collect(Collectors.toList());

        // 4. Enviar resultados a la vista
        model.addAttribute("filteredPrices", filteredPrices);
        return "gas-price-result";
    }
}