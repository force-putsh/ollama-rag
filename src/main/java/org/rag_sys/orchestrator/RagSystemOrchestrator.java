package org.rag_sys.orchestrator;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.rag_sys.config.RagConfiguration;
import org.rag_sys.factory.ServiceFactory;
import org.rag_sys.model.DocumentAnalyser;
import org.rag_sys.services.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Orchestrateur principal du système RAG
 * Principe SRP : Coordonne les différents services
 * Principe DIP : Dépend des abstractions, pas des implémentations
 */
public class RagSystemOrchestrator {
    
    private final DocumentLoaderService documentLoaderService;
    private final EmbeddingModelService embeddingModelService;
    private final VectorStoreService vectorStoreService;
    private final RagService ragService;
    private final UserInteractionService userInteractionService;
    private final RagConfiguration configuration;
    
    public RagSystemOrchestrator(RagConfiguration configuration) {
        this.configuration = configuration;
        ServiceFactory serviceFactory = new ServiceFactory(configuration);
        
        this.documentLoaderService = serviceFactory.createDocumentLoaderService();
        this.embeddingModelService = serviceFactory.createEmbeddingModelService();
        this.vectorStoreService = serviceFactory.createVectorStoreService();
        this.ragService = serviceFactory.createRagService();
        this.userInteractionService = serviceFactory.createUserInteractionService();
    }
    
    /**
     * Lance le système RAG complet
     */
    public void start() throws URISyntaxException {
        System.out.println("Initialisation du système RAG...");
        
        // 1. Charger les documents
        String documentsPath = getDocumentsPath();
        System.out.println("Chargement des documents depuis: " + documentsPath);
        List<Document> documents = documentLoaderService.loadDocuments(documentsPath);
        
        if (documents.isEmpty()) {
            System.out.println("Aucun document trouvé au chemin spécifié: " + documentsPath);
            return;
        }
        
        // 2. Créer le modèle d'embedding
        EmbeddingModel embeddingModel = embeddingModelService.createEmbeddingModel(configuration.getEmbeddingModel());
        
        // 3. Créer le vector store
        EmbeddingStore<TextSegment> embeddingStore = vectorStoreService.createVectorStore(documents, embeddingModel);
        if (embeddingStore == null) {
            System.out.println("Échec de la création du store d'embeddings.");
            return;
        }
        System.out.println("Store d'embeddings créé avec succès.");
        
        // 4. Configurer la chaîne RAG
        System.out.println("Configuration de la chaîne RAG avec le modèle: " + configuration.getModelName());
        System.out.println("Utilisation du modèle d'embedding: " + configuration.getEmbeddingModel());
        
        EmbeddingStoreContentRetriever retriever = ragService.createRetriever(embeddingStore, embeddingModel);
        DocumentAnalyser ragChain = ragService.setupRagChain(retriever, configuration.getModelName());
        
        System.out.println("Système RAG initialisé avec succès.");
        
        // 5. Démarrer la session interactive
        userInteractionService.startInteractiveSession(ragChain,retriever);
    }
    
    private String getDocumentsPath() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource("story/").toURI()).toString();
    }
}
