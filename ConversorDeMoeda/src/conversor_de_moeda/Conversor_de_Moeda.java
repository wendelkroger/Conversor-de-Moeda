package conversor_de_moeda;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Conversor_de_Moeda {

    private static Map<String, Double> taxasDeCambio;

    public static void main(String[] args) {
        atualizarTaxas();

        if (taxasDeCambio == null || taxasDeCambio.isEmpty()) {
            System.out.println("Não foi possível obter as taxas de câmbio. Verifique sua conexão ou tente novamente mais tarde.");
            return;
        }
        System.out.println("Escolha '1' para converter a quantidade de dinheiro em Dólar para todas as moedas disponíveis");
        System.out.println("Escolha '2' para converter a quantidade de dinheiro em Euro para todas as moedas disponíveis");
        System.out.println("Escolha '3' para converter a quantidade de dinheiro em Iene para todas as moedas disponíveis");
        System.out.println("Escolha '4' para converter a quantidade de dinheiro em Real para todas as moedas disponíveis");
        System.out.println("Escolha '5' para converter a quantidade de dinheiro em Peso Argentino para todas as moedas disponíveis");
        System.out.println();

        try (Scanner cv = new Scanner(System.in)) {
            System.out.println("Digite o número escolhido:");
            int escolha = cv.nextInt();
            System.out.println("Coloque o valor desejado:");
            double quantidade = cv.nextDouble();
            System.out.println();

            switch (escolha) {
                case 1:
                    exibirConversoes("USD", quantidade);
                    break;
                case 2:
                    exibirConversoes("EUR", quantidade);
                    break;
                case 3:
                    exibirConversoes("JPY", quantidade);
                    break;
                case 4:
                    exibirConversoes("BRL", quantidade);
                    break;
                case 5:
                    exibirConversoes("ARS", quantidade);
                    break;
                default:
                    System.out.println("Escolha entre os números 1, 2, 3, 4, 5 para conversão.");
            }
        }
    }

    private static void atualizarTaxas() {


        String apiUrl = "https://v6.exchangerate-api.com/v6/774563b9565ef0c1a0d7e066/latest/USD";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    StringBuilder jsonResponse = new StringBuilder();
                    while (scanner.hasNext()) {
                        jsonResponse.append(scanner.nextLine());
                    }

                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Object>>() {}.getType();
                    Map<String, Object> apiData = gson.fromJson(jsonResponse.toString(), type);

                    Map<String, Double> conversionRates = (Map<String, Double>) apiData.get("conversion_rates");
                    taxasDeCambio = conversionRates;
                }
            } else {
                System.out.println("Erro ao tentar buscar taxas de câmbio. Código de resposta: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao conectar com a API: " + e.getMessage());
        }
    }

    public static void exibirConversoes(String moedaOrigem, double quantidade) {

        Set<String> moedasPermitidas = Set.of("BRL", "USD", "EUR", "ARS", "JPY");

        if (taxasDeCambio != null && taxasDeCambio.containsKey(moedaOrigem)) {
            double taxaOrigem = taxasDeCambio.get(moedaOrigem);

            System.out.printf("Taxas de câmbio com base em %s:%n", moedaOrigem);
            for (Map.Entry<String, Double> entrada : taxasDeCambio.entrySet()) {
                String moedaDestino = entrada.getKey();
                if (moedasPermitidas.contains(moedaDestino)) {

                    double taxaDestino = entrada.getValue();

                    double taxaRelativa = taxaDestino / taxaOrigem;

                    double convertido = quantidade * taxaRelativa;

                    System.out.printf("1 %s custa %.6f em %s%n", moedaOrigem, taxaRelativa, moedaDestino);
                    System.out.printf("%.2f %s = %.2f %s%n", quantidade, moedaOrigem, convertido, moedaDestino);
                    System.out.println("----------------------");
                }
            }
        } else {
            System.out.println("Não há taxas de câmbio disponíveis para a moeda selecionada.");
        }
    }
}


