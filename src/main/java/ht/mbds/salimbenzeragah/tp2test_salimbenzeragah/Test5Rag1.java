package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
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

public class Test5Rag1 {

    interface Assistant {
        String chat(String message);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : La variable d'environnement GEMINI_KEY est absente.");
            return;
        }

        Path fichier = Path.of("infos.txt");

        if (!Files.isRegularFile(fichier)) {
            System.err.println("Erreur : Le fichier infos.txt est introuvable à la racine du projet.");
            return;
        }

        // 1. Chargement du document texte
        Document document = FileSystemDocumentLoader.loadDocument(fichier.toAbsolutePath());

        // 2. Modèle d'embeddings Google GenAI
        EmbeddingModel embeddingModel = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .timeout(Duration.ofSeconds(15))
                .build();

        // 3. Stockage vectoriel en mémoire
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // 4. Ingestion du document
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build();

        ingestor.ingest(document);

        // 5. Retriever RAG
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .maxResults(2)
                .build();

        // 6. Initialisation du LLM avec température à 0.3 (pour plus d'exactitude)
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.3)
                .build();

        // 7. Assistant avec mémoire conversationnelle et RAG
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        // --- Question 1 : Recherche d'information directe dans le document ---
        String q1 = "Comment s'appelle le chat de Pierre ?";
        System.out.println("Question 1 : " + q1);
        System.out.println("Réponse    : " + assistant.chat(q1));
        System.out.println();

        // --- Question 2 : Combinaison RAG + Capacités linguistiques ---
        String q2 = "Pierre appelle son chat. Qu'est-ce qu'il pourrait dire ?";
        System.out.println("Question 2 : " + q2);
        System.out.println("Réponse    : " + assistant.chat(q2));
        System.out.println();

        // --- Question 3 : Modification de la capitale (Marseille au lieu de Paris) ---
        String q3 = "Quelle est la capitale de la France ?";
        System.out.println("Question 3 : " + q3);
        System.out.println("Réponse    : " + assistant.chat(q3));
    }
}