package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MeteoTool {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool("Vérifie la probabilité de pluie pour un lieu défini par sa latitude et sa longitude pour les 3 prochains jours. Retourne vrai si la pluie dépasse le seuil.")
    public boolean isPluiePrevue(
            @P("Latitude de la ville (ex: 48.85 pour Paris)") double latitude,
            @P("Longitude de la ville (ex: 2.35 pour Paris)") double longitude) {

        System.out.println("  [OUTIL EXECUTE] Recherche météo pour lat=" + latitude + ", lon=" + longitude);
        int seuilPluie = 50; // Seuil en pourcentage

        try {
            String url = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%.2f&longitude=%.2f&daily=precipitation_probability_max&forecast_days=3",
                    latitude, longitude
            ).replace(",", ".");

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode probabilities = root.path("daily").path("precipitation_probability_max");

            for (JsonNode proba : probabilities) {
                if (proba.asInt() >= seuilPluie) {
                    System.out.println("  [OUTIL EXECUTE] Pluie détectée (" + proba.asInt() + "% >= " + seuilPluie + "%)");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("  [OUTIL ERREUR] " + e.getMessage());
        }
        return false;
    }

    @Tool("Obtient les coordonnées géographiques (latitude et longitude) d'une ville à partir de son nom en anglais.")
    public double[] getCoordonnees(@P("Nom de la ville (de préférence en anglais)") String nomVille) {
        System.out.println("  [OUTIL EXECUTE] Géolocalisation pour : " + nomVille);
        try {
            String url = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                    java.net.URLEncoder.encode(nomVille, "UTF-8") + "&count=1&language=en&format=json";

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode results = root.path("results");

            if (results.isArray() && results.size() > 0) {
                double lat = results.get(0).path("latitude").asDouble();
                double lon = results.get(0).path("longitude").asDouble();
                System.out.println("  [OUTIL EXECUTE] Coordonnées trouvées : lat=" + lat + ", lon=" + lon);
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.err.println("  [OUTIL ERREUR] " + e.getMessage());
        }
        System.out.println("  [OUTIL EXECUTE] Aucune coordonnée trouvée pour " + nomVille);
        return new double[]{0.0, 0.0};
    }
}