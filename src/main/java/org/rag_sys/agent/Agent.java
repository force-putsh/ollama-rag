package org.rag_sys.agent;

import org.rag_sys.model.DocumentAnalyser;

/**
 * Interface représentant un agent spécialisé dans le système RAG
 */
public interface Agent {


    /**
     * Traite une question avec cet agent spécialisé
     * @param question la question à traiter
     * @return la réponse de l'agent
     */
    String processQuestion(String question);
    
    /**
     * Indique si l'agent est prêt à traiter des questions
     */
    boolean isReady();

}
