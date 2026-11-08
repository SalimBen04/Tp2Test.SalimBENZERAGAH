package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;
import java.util.List;

public class Test4Embeddings {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : La variable GEMINI_KEY est absente.");
            return;
        }

        // Modèle d'embeddings Gemini de Google
        EmbeddingModel googleModel = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .timeout(Duration.ofSeconds(10))
                .build();

        // 6 couples de phrases à tester (similaires, opposées, sans rapport)
        List<String[]> couples = List.of(
                new String[]{"Le chat dort sur le canapé.", "Un félin fait la sieste sur le sofa."},
                new String[]{"J'aime le chocolat.", "Je déteste le chocolat."},
                new String[]{"La capitale de la France est Paris.", "Paris est la ville lumière."},
                new String[]{"Il fait très beau et ensoleillé aujourd'hui.", "La pluie tombe abondamment dehors."},
                new String[]{"LangChain4j permet d'utiliser des LLM en Java.", "Java est un langage de programmation orienté objet."},
                new String[]{"La fusée décolle vers l'espace.", "La recette des pâtes au pesto."}
        );

        System.out.println("=== Calcul de similarité cosinus avec Google GenAI Embedding ===\n");

        int index = 1;
        for (String[] couple : couples) {
            String p1 = couple[0];
            String p2 = couple[1];

            // Génération des embeddings et calcul de la similarité cosinus
            Embedding e1 = googleModel.embed(p1).content();
            Embedding e2 = googleModel.embed(p2).content();
            double similarity = CosineSimilarity.between(e1, e2);

            System.out.printf("Couple %d :%n", index++);
            System.out.println("  Phrase 1   : " + p1);
            System.out.println("  Phrase 2   : " + p2);
            System.out.printf("  Similarité : %.4f%n%n", similarity);
        }
    }
}