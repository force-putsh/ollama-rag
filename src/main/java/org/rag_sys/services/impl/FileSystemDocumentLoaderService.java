package org.rag_sys.services.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import org.rag_sys.services.DocumentLoaderService;

import java.util.List;

/**
 * Implémentation du service de chargement de documents
 * Principe SRP : Se concentre uniquement sur le chargement des documents
 */
public class FileSystemDocumentLoaderService implements DocumentLoaderService {
    
    private static final String DEFAULT_MODEL_NAME = "mistral";
    private static final String DEFAULT_EMBEDDING_MODEL = "nomic-embed-text";
    
    @Override
    public List<Document> loadDocuments(String path) {
        try {
            List<Document> documents = FileSystemDocumentLoader.loadDocuments(path, new TextDocumentParser());
            
            if (documents.isEmpty()) {
                throw new RuntimeException("No documents found at the specified path: " + path);
            }
            
            System.out.println("Loaded " + documents.size() + " documents from path: " + path);
            
            // Ajouter les métadonnées
            documents.forEach(document -> {
                document.metadata().put("source", path);
                document.metadata().put("modelName", DEFAULT_MODEL_NAME);
                document.metadata().put("embeddingModel", DEFAULT_EMBEDDING_MODEL);
            });
            
            return documents;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load documents from path: " + path, e);
        }
    }
}
