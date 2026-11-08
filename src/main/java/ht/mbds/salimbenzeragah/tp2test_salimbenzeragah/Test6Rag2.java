package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Scanner;

public class Test6Rag2 {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : La variable d'environnement GEMINI_KEY est absente.");
            return;
        }

        String nomFichierPdf = "cours.pdf";
        Path pdfPath = Path.of(nomFichierPdf);

        if (!Files.isRegularFile(pdfPath)) {
            System.err.println("Erreur : Le fichier '" + nomFichierPdf + "' est introuvable à la racine du projet.");
            return;
        }

        System.out.println("=== TEST 6 : RAG AVEC DOCUMENT PDF ===");
        System.out.println("Chargement et indexation du document PDF en cours...");

        // 1. Chargement direct du PDF (sans besoin de parser Tika externe)
        Document document = FileSystemDocumentLoader.loadDocument(pdfPath.toAbsolutePath());

        // 2. Modèle d'embeddings Google GenAI avec timeout rallongé
        EmbeddingModel embeddingModel = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .timeout(Duration.ofSeconds(120))
                .build();

        // 3. Stockage et découpage en grands blocs (5000 chars) pour éviter la saturation du quota
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(5000, 200))
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build();

        ingestor.ingest(document);
        System.out.println("Indexation terminée avec succès !\n");

        // 4. Retriever RAG
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .maxResults(3)
                .build();

        // 5. Modèle Chat Gemini
        GoogleGenAiChatModel chatModel = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.3)
                .build();

        // 6. Assistant conversationnel
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .contentRetriever(retriever)
                .build();

        // 7. Démarrage de la conversation
        conversationAvec(assistant);
    }

    private static void conversationAvec(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question : ");
                String question = scanner.nextLine();
                if (question.isBlank()) {
                    continue;
                }
                System.out.println("==================================================");
                if ("fin".equalsIgnoreCase(question.trim()) || "quit".equalsIgnoreCase(question.trim())) {
                    System.out.println("Fin de la conversation.");
                    break;
                }
                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}