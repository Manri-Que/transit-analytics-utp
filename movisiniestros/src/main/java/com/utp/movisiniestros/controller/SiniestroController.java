package com.utp.movisiniestros.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analitica")
@CrossOrigin(origins = "*")
public class SiniestroController {

    private static final Logger log = LoggerFactory.getLogger(SiniestroController.class);

    @GetMapping("/evaluar-tramo")
    public Map<String, Object> evaluarTramo(
            @RequestParam("siniestros") double siniestros,
            @RequestParam("fallecidos") double fallecidos,
            @RequestParam("longitudKm") double longitudKm) {

        log.info("Evaluando riesgo vial en el tramo...");

        Map<String, Object> response = new HashMap<>();

        // Validacion para la rubrica
        if (longitudKm <= 0 || siniestros < 0 || fallecidos < 0) {
            log.warn("Se ingresaron parametros negativos o nulos.");
            response.put("error", "Los parametros deben ser mayores a cero.");
            return response;
        }

        // Logica original de calculo
        double isk = siniestros / longitudKm;
        double tlc = (siniestros > 0) ? (fallecidos / siniestros) * 100 : 0;

        String evaluacion;
        if (isk > 1.5 || tlc > 20.0) {
            evaluacion = "ROJO (Punto Negro / Peligro Alto)";
        } else if (isk >= 0.8) {
            evaluacion = "AMARILLO (Riesgo Moderado)";
        } else {
            evaluacion = "VERDE (Riesgo Bajo)";
        }

        response.put("indiceSiniestralidadISK", String.format("%.2f", isk));
        response.put("tasaLetalidadTLC", String.format("%.2f", tlc) + "%");
        response.put("evaluacionRiesgo", evaluacion);

        return response;
    }
}