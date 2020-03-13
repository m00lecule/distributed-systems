package com.example.rest;

import com.google.common.math.Quantiles;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Controller
public class FinController {

    final static ExecutorService service = Executors.newCachedThreadPool();

    @GetMapping(value = "/convert", produces = MediaType.TEXT_HTML_VALUE)
    public String convert(@RequestParam(name = "convertTo") String convertTo, @RequestParam(name = "base") String base, Model model) {

        if (convertTo.equals(base)) {
            model.addAttribute("from", base);
            model.addAttribute("to", convertTo);
            model.addAttribute("avg", 1);
            model.addAttribute("min", 1);
            model.addAttribute("max", 1);
            model.addAttribute("med", 1);

            return "greeting";
        }

        Map<String, String> stats = calculateStatistics(base, convertTo);

        model.addAttribute("from", base);
        model.addAttribute("to", convertTo);
        model.addAttribute("avg", stats.get("avg"));
        model.addAttribute("min", stats.get("min"));
        model.addAttribute("max", stats.get("max"));
        model.addAttribute("med", stats.get("med"));

        return "greeting";
    }

    @GetMapping(value = "/convert", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String greetingJson(@RequestParam(name = "convertTo") String convertTo, @RequestParam(name = "base") String base, Model model) {
        if (base.equals(convertTo)) {
            Map<String, String> stats = new HashMap<>();
            stats.put("from", base);
            stats.put("to", convertTo);
            stats.put("avg", "1");
            stats.put("min", "1");
            stats.put("max", "1");
            stats.put("med", "1");
            return new JSONObject(stats).toString();
        }

        Map<String, String> stats = calculateStatistics(base, convertTo);
        stats.put("from", base);
        stats.put("to", convertTo);

        return new JSONObject(stats).toString();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    private Map<String, String> calculateStatistics(String base, String convertTo) {
        final List<Double> exchangeRates = new CopyOnWriteArrayList<>();

        List<Runnable> tasks = new ArrayList<>();

        tasks.add(() -> {
            try {
                final RestTemplate restTemplate = new RestTemplate();
                UriComponents uriComponents = UriComponentsBuilder.newInstance()
                        .scheme("https")
                        .host("api.worldtradingdata.com/api/v1/forex_history")
                        .queryParam("api_token", "H0yOJEp6K7t8RWrQei4WBrEKeD1wQ72582MhDyzjqGMTIww1Dz4JQ3JKG22u")
                        .queryParam("base", base)
                        .queryParam("convert_to", convertTo)
                        .build();
                exchangeRates.addAll(new JSONObject(restTemplate.getForObject(uriComponents.toString(), String.class)).getJSONObject("history").toMap().values().stream().mapToDouble(obj -> {
                    if (obj instanceof String)
                        return Double.parseDouble(String.valueOf(obj));
                    else if (obj instanceof Double)
                        return (double) obj;
                    return 0;
                }).boxed().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        tasks.add(() -> {
            final RestTemplate restTemplate = new RestTemplate();
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("api.frankfurter.app/latest")
                    .queryParam("amount", "1")
                    .queryParam("from", base)
                    .queryParam("to", convertTo)
                    .build();

            JSONObject jsonObject = new JSONObject(restTemplate.getForObject(uriComponents.toString(), String.class));

            exchangeRates.add((Double) jsonObject.getJSONObject("rates").get(convertTo));

        });

        tasks.add(() -> {
            final RestTemplate restTemplate = new RestTemplate();
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("api.exchangeratesapi.io/latest")
                    .queryParam("base", base)
                    .build();

            JSONObject jsonObject = new JSONObject(restTemplate.getForObject(uriComponents.toString(), String.class));

            exchangeRates.add((Double) jsonObject.getJSONObject("rates").get(convertTo));
        });

        tasks.add(() -> {
            RestTemplate restTemplate = new RestTemplate();
            UriComponents uriComponents = UriComponentsBuilder.newInstance()
                    .scheme("https").host("prime.exchangerate-api.com/v5/c9f96f49da71872af956afb0/latest").path(base).build();

            JSONObject jsonObject = new JSONObject(restTemplate.getForObject(uriComponents.toString(), String.class));
            exchangeRates.add((Double) jsonObject.getJSONObject("conversion_rates").get(convertTo));
        });

        CompletableFuture<?>[] futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, service))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();

        Collections.sort(exchangeRates);

        Map<String, String> stats = new HashMap<>();
        stats.put("avg", ((Double) exchangeRates.stream().mapToDouble(a -> (Double) a).average().getAsDouble()).toString());
        stats.put("min", exchangeRates.get(0).toString());
        stats.put("max", exchangeRates.get(exchangeRates.size() - 1).toString());
        stats.put("med", ((Double) Quantiles.median().compute(exchangeRates)).toString());

        return stats;
    }
}