package org.rag_sys.services;

import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.rag_sys.model.DocumentAnalyser;

/**
 * Service responsable de la configuration de la chaîne RAG
 * Principe SRP : Une seule responsabilité - configurer RAG
 */
public interface RagService {
    /**
     * Configure la chaîne RAG
     * @param retriever le retriever de contenu
     * @param modelName nom du modèle de chat
     * @return l'analyseur de documents configuré
     */
    DocumentAnalyser setupRagChain(EmbeddingStoreContentRetriever retriever, String modelName);
    
    /**
     * Crée un retriever de contenu
     * @param embeddingStore le store d'embeddings
     * @param embeddingModel le modèle d'embedding
     * @return le retriever configuré
     */
    EmbeddingStoreContentRetriever createRetriever(
        dev.langchain4j.store.embedding.EmbeddingStore<dev.langchain4j.data.segment.TextSegment> embeddingStore, 
        dev.langchain4j.model.embedding.EmbeddingModel embeddingModel
    );
}
