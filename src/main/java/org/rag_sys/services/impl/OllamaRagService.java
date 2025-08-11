package org.rag_sys.services.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.rag_sys.model.DocumentAnalyser;
import org.rag_sys.services.RagService;

/**
 * Implémentation du service RAG utilisant Ollama
 * Principe SRP : Se concentre uniquement sur la configuration RAG
 * Principe OCP : Peut être étendu pour supporter d'autres modèles de chat
 */
public class OllamaRagService implements RagService {
    
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final double DEFAULT_TEMPERATURE = 0.2;
    private static final int DEFAULT_MAX_MESSAGES = 10;
    
    private final String baseUrl;
    private final double temperature;
    private final int maxMessages;
    
    public OllamaRagService() {
        this(DEFAULT_BASE_URL, DEFAULT_TEMPERATURE, DEFAULT_MAX_MESSAGES);
    }
    
    public OllamaRagService(String baseUrl, double temperature, int maxMessages) {
        this.baseUrl = baseUrl;
        this.temperature = temperature;
        this.maxMessages = maxMessages;
    }
    
    @Override
    public DocumentAnalyser setupRagChain(EmbeddingStoreContentRetriever retriever, String modelName) {
        OllamaChatModel ollamaModel = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .responseFormat(ResponseFormat.JSON)
                .temperature(temperature)
                .build();

        return AiServices.builder(DocumentAnalyser.class)
                .chatModel(ollamaModel)
                .contentRetriever(retriever)
                .chatMemory(MessageWindowChatMemory.builder().maxMessages(maxMessages).build())
                .build();
    }
    
    @Override
    public EmbeddingStoreContentRetriever createRetriever(
            EmbeddingStore<TextSegment> embeddingStore, 
            EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
    }
}
