package org.rag_sys.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import java.util.List;

/**
 * Service responsable de la création et gestion du vector store
 * Principe SRP : Une seule responsabilité - gérer le vector store
 */
public interface VectorStoreService {
    /**
     * Crée un vector store à partir des documents et du modèle d'embedding
     * @param documents les documents à stocker
     * @param embeddingModel le modèle d'embedding à utiliser
     * @return le vector store créé
     */
    EmbeddingStore<TextSegment> createVectorStore(List<Document> documents, EmbeddingModel embeddingModel);
}
