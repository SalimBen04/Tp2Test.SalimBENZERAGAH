package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;

public class Test2Couts {

    // Prix en dollars pour 1 million de tokens d'après la grille tarifaire
    private static final double PRIX_ENTREE_PAR_MILLION = 0.30;
    private static final double PRIX_SORTIE_PAR_MILLION = 2.50;

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // Cette méthode renvoie un ChatResponse, qui contient la réponse et les informations d'usage.
        ChatResponse response = model.chat(UserMessage.from("Donne moi la capitale du Maroc"));

        System.out.println("Réponse : " + response.aiMessage().text());

        TokenUsage usage = response.tokenUsage();
        int entree = usage.inputTokenCount();
        int sortie = usage.outputTokenCount();
        System.out.println("Tokens d'entrée : " + entree);
        System.out.println("Tokens de sortie : " + sortie);
        System.out.println("Total : " + usage.totalTokenCount());

        double cout = entree / 1_000_000.0 * PRIX_ENTREE_PAR_MILLION
                + sortie / 1_000_000.0 * PRIX_SORTIE_PAR_MILLION;
        System.out.println("Coût de la requête : " + cout + " $");

        if (cout > 0) {
            System.out.println("Requêtes similaires pour dépenser 1 $ : " + (1 / cout));
        }
    }
}