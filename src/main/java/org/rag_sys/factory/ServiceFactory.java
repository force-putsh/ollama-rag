package org.rag_sys.factory;

import org.rag_sys.config.RagConfiguration;
import org.rag_sys.services.*;
import org.rag_sys.services.impl.*;

/**
 * Factory pour créer les services
 * Principe DIP : Créer les dépendances nécessaires
 * Principe SRP : Se concentre uniquement sur la création des services
 */
public class ServiceFactory {
    
    private final RagConfiguration configuration;
    
    public ServiceFactory(RagConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public DocumentLoaderService createDocumentLoaderService() {
        return new FileSystemDocumentLoaderService();
    }
    
    public EmbeddingModelService createEmbeddingModelService() {
        return new OllamaEmbeddingModelService(configuration.getOllamaBaseUrl(), true);
    }
    
    public VectorStoreService createVectorStoreService() {
        return new PgVectorStoreService(
            configuration.getPostgresHost(),
            configuration.getPostgresPort(),
            configuration.getPostgresUser(),
            configuration.getPostgresPassword(),
            configuration.getPostgresDatabase(),
            configuration.getPostgresTable()
        );
    }
    
    public RagService createRagService() {
        return new OllamaRagService(configuration.getOllamaBaseUrl(), 0.2, 10);
    }
    
    public UserInteractionService createUserInteractionService() {
        return new ConsoleUserInteractionService();
    }
}
