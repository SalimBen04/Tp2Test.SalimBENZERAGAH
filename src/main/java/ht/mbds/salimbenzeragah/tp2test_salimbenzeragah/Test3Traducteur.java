package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah; // Adaptez selon votre package

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

public class Test3Traducteur {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // 1. Template à 1 paramètre (Traduction en anglais)
        PromptTemplate templateSimple = PromptTemplate.from("Traduis le texte suivant en anglais : {{texte}}");

        Map<String, Object> variablesSimple = new HashMap<>();
        variablesSimple.put("texte", "Bonjour, comment allez-vous aujourd'hui ?");

        Prompt prompt1 = templateSimple.apply(variablesSimple);
        String reponse1 = model.chat(prompt1.text());

        System.out.println("=== Test 1 paramètre ===");
        System.out.println("Texte généré par le template : " + prompt1.text());
        System.out.println("Réponse du LLM : " + reponse1);
        System.out.println();

        // 2. Template à 2 paramètres (Langue cible + Texte)
        PromptTemplate templateDouble = PromptTemplate.from("Traduis le texte suivant en {{langue}} : {{texte}}");

        Map<String, Object> variablesDouble = new HashMap<>();
        variablesDouble.put("langue", "espagnol");
        variablesDouble.put("texte", "Je suis Salim et je suis en train d'apprendre LangChain4j avec Java.");

        Prompt prompt2 = templateDouble.apply(variablesDouble);
        String reponse2 = model.chat(prompt2.text());

        System.out.println("=== Test 2 paramètres ===");
        System.out.println("Texte généré par le template : " + prompt2.text());
        System.out.println("Réponse du LLM : " + reponse2);
    }
}