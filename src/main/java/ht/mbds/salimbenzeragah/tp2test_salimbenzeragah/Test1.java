package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;   // à adapter à ton package

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

public class Test1 {

    public static void main(String[] args) {
        // La clé secrète est lue dans la variable d'environnement, jamais écrite dans le code.
        String cle = System.getenv("GEMINI_KEY");

        // Le modèle est créé avec le pattern "builder".
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        String reponse = model.chat("Mon nom c  est quoi ?");
        System.out.println(reponse);
    }
}