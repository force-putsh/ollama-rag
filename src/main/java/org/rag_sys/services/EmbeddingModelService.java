package org.rag_sys.services;

import dev.langchain4j.model.embedding.EmbeddingModel;

/**
 * Service responsable de la création des modèles d'embedding
 * Principe SRP : Une seule responsabilité - créer les modèles d'embedding
 */
public interface EmbeddingModelService {
    /**
     * Crée un modèle d'embedding
     * @param modelName nom du modèle
     * @return le modèle d'embedding créé
     */
    EmbeddingModel createEmbeddingModel(String modelName);
}
