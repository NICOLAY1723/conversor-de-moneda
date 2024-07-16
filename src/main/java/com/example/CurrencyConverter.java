package com.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Scanner;

public class CurrencyConverter {

    private static final String API_KEY = "e349dba00e02614b6fa9de4a";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa la cantidad que deseas convertir: ");
        double amount = scanner.nextDouble();

        System.out.print("Ingresa el tipo de moneda que es el monto : (ejemplo, USD, EUR, JPY, GBP, AUD, CAD, CHF, CNY, SEK, NZD, MXN, SGD, HKD, NOK, KRW, TRY, RUB, INR, BRL, ZAR): ");
        String fromCurrency = scanner.next().toUpperCase();

        System.out.print("Ingresa a la moneda que deseas convertir: (ejemplo, USD, EUR, JPY, GBP, AUD, CAD, CHF, CNY, SEK, NZD, MXN, SGD, HKD, NOK, KRW, TRY, RUB, INR, BRL, ZAR): ");
        String toCurrency = scanner.next().toUpperCase();

        try {
            double rate = getExchangeRate(fromCurrency, toCurrency);
            double convertedAmount = convertCurrency(amount, rate);
            System.out.printf("%.2f %s es igual a: %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
        } catch (Exception e) {
            System.err.println("error en la conversion: " + e.getMessage());
        }

        scanner.close();
    }

    private static double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        String url = BASE_URL + fromCurrency;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to get exchange rate.");
        }
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
        if (conversionRates == null || !conversionRates.has(toCurrency)) {
            throw new Exception("Failed to get exchange rate from " + fromCurrency + " to " + toCurrency);
        }
        return conversionRates.get(toCurrency).getAsDouble();
    }

    private static double convertCurrency(double amount, double rate) {
        return amount * rate;
    }
}
