package org.rag_sys.services.impl;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.rag_sys.services.EmbeddingModelService;

/**
 * Implémentation du service de modèles d'embedding utilisant Ollama
 * Principe SRP : Se concentre uniquement sur la création des modèles d'embedding
 * Principe OCP : Peut être étendu pour supporter d'autres providers
 */
public class OllamaEmbeddingModelService implements EmbeddingModelService {
    
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private final String baseUrl;
    private final boolean logRequests;
    
    public OllamaEmbeddingModelService() {
        this(DEFAULT_BASE_URL, true);
    }
    
    public OllamaEmbeddingModelService(String baseUrl, boolean logRequests) {
        this.baseUrl = baseUrl;
        this.logRequests = logRequests;
    }
    
    @Override
    public EmbeddingModel createEmbeddingModel(String modelName) {
        return new OllamaEmbeddingModel.OllamaEmbeddingModelBuilder()
                .baseUrl(baseUrl)
                .logRequests(logRequests)
                .modelName(modelName)
                .build();
    }
}
