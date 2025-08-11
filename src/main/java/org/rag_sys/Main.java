package org.rag_sys;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.rag_sys.model.DocumentAnalyser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class Main {
    public static void main(String[] args) throws URISyntaxException {

        System.out.println("Initialisation du système RAG...");

        var path = Paths.get(Main.class.getClassLoader().getResource("story/").toURI());
        var modelName = "mistral";
        var embeddingModel = "nomic-embed-text";
//        var question = "parle moi de l'Opération Cyclone";

        System.out.println("Loading documents from path: " + path);
        var documents = loadDocuments(path.toString());
        if (documents.isEmpty()) {
            System.out.println("No documents found at the specified path: " + path);
            return;
        }

        // create vector store
        EmbeddingModel embeddingModelInstance = createEmbeddingModel(embeddingModel);
        EmbeddingStore embeddingStore = createVectorStore(documents, embeddingModelInstance);
        if (embeddingStore == null) {
            System.out.println("Failed to create embedding store.");
            return;
        }
        System.out.println("Embedding store created successfully.");
        // setup RAG chain
        System.out.println("Setting up RAG chain with model: " + modelName);
        System.out.println("Using embedding model: " + embeddingModel);
        EmbeddingStoreContentRetriever retriever = getRetriever(embeddingStore, embeddingModelInstance);
        DocumentAnalyser ragChain=setupRagChain(retriever, modelName);
        System.out.println("RAG system initialised successfully.");

        System.out.println("To use the RAG system, you can now ask questions about the documents.");
        System.out.println("Type your question and press Enter. Type 'exit' or 'quit' to exit the application.");
        Scanner scanner = new Scanner(System.in);

        do {
            // Read user input
            System.out.print("Question: ");
            String question = scanner.nextLine();
            if (question == null || question.equalsIgnoreCase("exit") || question.equalsIgnoreCase("quit")) {
                System.out.println("Exiting the application.");
                break;
            }

            // Create a prompt using the question
            Prompt prompt = PromptTemplate.from(DocumentAnalyser.promptTemplate).apply(Map.of(
                    "question", question,
                    "context", retriever
            ));

            // Get the response from the RAG chain
            String response = ragChain.analyse(prompt.text());
            System.out.println("Response: " + response);
        } while (true);



    }

    private static List<Document> loadDocuments(String path) {
        try {
            List<Document> documents = FileSystemDocumentLoader.loadDocuments(path,new TextDocumentParser());
            if (documents.isEmpty()) {
                System.out.println("document is empty");
                throw new RuntimeException("No documents found at the specified path: " + path);
            }
            System.out.println("Loaded " + documents.size() + " documents from path: " + path);

            // add metadata
            documents.forEach(document -> {
                document.metadata().put("source", path);
                document.metadata().put("modelName", "mistral");
                document.metadata().put("embeddingModel", "nomic-embed-text");
            });

            return documents;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load documents from path: " + path, e);
        }

    }
    // create vector store using ollama embedding model and pgvector store

    private static EmbeddingModel createEmbeddingModel(String embeddingModelName) {
        EmbeddingModel embeddingModelInstance = new OllamaEmbeddingModel
                .OllamaEmbeddingModelBuilder()
                .baseUrl("http://localhost:11434")
                .logRequests(true)
                .modelName(embeddingModelName).build();
        return  embeddingModelInstance;
    }

    private static EmbeddingStore createVectorStore(List<Document> documents, EmbeddingModel embeddingModelInstance) {
        DockerImageName dockerImageName = DockerImageName.parse("pgvector/pgvector:pg17");
        try (var pgVectorContainer = new PostgreSQLContainer<>(dockerImageName)) {
//            pgVectorContainer.start();

            // create text segments from documents
            List<TextSegment> textSegments = new ArrayList<TextSegment>();

            for (Document document : documents) {
                TextSegment textSegment = document.toTextSegment();
                textSegments.add(textSegment);
            }

            List<Embedding> embeddings= embeddingModelInstance.embedAll(textSegments).content();
            // create pgvector store
            EmbeddingStore embeddingStore = PgVectorEmbeddingStore.builder()
                    .host("localhost")
                    .port(5432)
                    .user("postgres")
                    .password("password")
                    .table("rag_embeddings")
                    .database("postgres")
                    .dimension(embeddingModelInstance.dimension())
                    .build();
//            embeddings.addAll(embeddings);

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(DocumentSplitters.recursive(1000,200))
                    .embeddingModel(embeddingModelInstance)
                    .embeddingStore(embeddingStore)
                    .build();
            ingestor.ingest(documents);

            return embeddingStore;
        }

    }


    private static DocumentAnalyser setupRagChain(EmbeddingStoreContentRetriever retriever, String modelName) {
        OllamaChatModel ollamaModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(modelName)
                .responseFormat(ResponseFormat.JSON)
                .temperature(0.2)
                .build();

        var ragChain = AiServices.builder(DocumentAnalyser.class)
                .chatModel(ollamaModel)
                .contentRetriever(retriever)
                .chatMemory(MessageWindowChatMemory.builder().maxMessages(10).build())
                .build();

        return ragChain;

    }

    private static EmbeddingStoreContentRetriever getRetriever(EmbeddingStore embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
    }

}