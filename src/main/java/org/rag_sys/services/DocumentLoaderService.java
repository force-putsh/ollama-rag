package org.rag_sys.services;

import dev.langchain4j.data.document.Document;
import java.util.List;

/**
 * Service responsable du chargement des documents
 * Principe SRP : Une seule responsabilité - charger les documents
 */
public interface DocumentLoaderService {
    /**
     * Charge les documents depuis un chemin donné
     * @param path le chemin vers les documents
     * @return la liste des documents chargés
     */
    List<Document> loadDocuments(String path);
}
