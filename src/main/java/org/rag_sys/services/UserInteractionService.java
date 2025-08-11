package org.rag_sys.services;

import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.rag_sys.model.DocumentAnalyser;

/**
 * Service responsable de l'interaction utilisateur
 * Principe SRP : Une seule responsabilité - gérer l'interface utilisateur
 */
public interface UserInteractionService {
    /**
     * Lance la session interactive avec l'utilisateur
     * @param documentAnalyser l'analyseur de documents à utiliser
     */
    void startInteractiveSession(DocumentAnalyser documentAnalyser, EmbeddingStoreContentRetriever retriever);
}
