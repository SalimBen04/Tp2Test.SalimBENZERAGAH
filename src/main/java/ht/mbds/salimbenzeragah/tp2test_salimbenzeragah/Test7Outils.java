package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

public class Test7Outils {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : La variable d'environnement GEMINI_KEY est absente.");
            return;
        }

        System.out.println("=== TEST 7 : UTILISATION D'OUTILS (TOOL CALLING) ===");

        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.0)
                .logRequestsAndResponses(true) // Pour voir les échanges JSON dans les logs
                .build();

        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(new MeteoTool())
                .build();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n==================================================");
                System.out.println("Posez votre question (ou 'fin' / 'quit') : ");
                String question = scanner.nextLine();

                if (question.isBlank()) continue;
                if ("fin".equalsIgnoreCase(question.trim()) || "quit".equalsIgnoreCase(question.trim())) {
                    System.out.println("Fin de la conversation.");
                    break;
                }

                String reponse = assistant.chat(question);
                System.out.println("\nAssistant : " + reponse);
            }
        }
    }
}