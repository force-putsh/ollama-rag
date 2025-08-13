package org.rag_sys.orchestrator;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.rag_sys.agent.AgentRouter;
import org.rag_sys.agent.AgentType;
import org.rag_sys.config.RagConfiguration;
import org.rag_sys.factory.ServiceFactory;
import org.rag_sys.model.DocumentAnalyser;
import org.rag_sys.services.*;
import org.rag_sys.services.impl.AgentUserInteractionService;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Orchestrateur principal du système RAG
 */
public class RagSystemOrchestrator {
    
    private final UserInteractionService userInteractionService;
//    private final ServiceFactory serviceFactory;
    private final AgentRouter agentRouter;
//    private final RagConfiguration configuration;
    
    public RagSystemOrchestrator(RagConfiguration configuration) {
//        this.configuration = configuration;
        this.agentRouter = new AgentRouter(configuration);
        this.userInteractionService = this.createAgentUserInteractionService(agentRouter);
    }
    
    /**
     * Lance le système RAG complet avec agents spécialisés
     */
    public void start() throws URISyntaxException {
        System.out.println("🚀 Initialisation du système RAG avec agents spécialisés...");

        // Initialiser les agents spécialisés
        initializeAgents();
        
        // Démarrer la session interactive
        System.out.println("✅ Système prêt ! Démarrage de la session interactive...");
        userInteractionService.startInteractiveSession(null, null);
    }
    
    private String getDocumentsPath(String directory) throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource(directory+ "/").toURI()).toString();
    }

    public DocumentAnalyser registerRagChainForDifferentAgents(String agentDirectory,ServiceFactory serviceFactory, RagConfiguration configuration){
        DocumentLoaderService documentLoaderService= serviceFactory.createDocumentLoaderService();
        EmbeddingModelService embeddingModelService = serviceFactory.createEmbeddingModelService();
        VectorStoreService vectorStoreService = serviceFactory.createVectorStoreService();
        RagService ragService = serviceFactory.createRagService();

        System.out.println("Chargement des documents pour l'agent: " + agentDirectory);
        var documents=loadDocuments(documentLoaderService, agentDirectory);
        EmbeddingModel embeddingModel = embeddingModelService.createEmbeddingModel(configuration.getEmbeddingModel());

        System.out.println("Création du store d'embeddings pour l'agent: " + agentDirectory);
        EmbeddingStore<TextSegment> embeddingStore = vectorStoreService.createVectorStore(documents, embeddingModel);
        if (embeddingStore == null) {
            System.out.println("Échec de la création du store d'embeddings.");
            throw new RuntimeException("Échec de la création du store d'embeddings.");
        }
        System.out.println("Store d'embeddings créé avec succès.");

        System.out.println("Configuration de la chaîne RAG pour l'agent: " + agentDirectory);
        EmbeddingStoreContentRetriever retriever = ragService.createRetriever(embeddingStore, embeddingModel);
        DocumentAnalyser ragChain = ragService.setupRagChain(retriever, configuration.getModelName());
        System.out.println("Chaîne RAG configurée avec succès pour l'agent: " + agentDirectory);
        return ragChain;
    }

    private List<Document> loadDocuments(DocumentLoaderService documentLoaderService ,String directory){
        try {
            String documentsPath = getDocumentsPath(directory);
            System.out.println("Chargement des documents depuis: " + documentsPath);
            List<Document> documents = documentLoaderService.loadDocuments(documentsPath);

            if (documents.isEmpty()) {
                System.out.println("Aucun document trouvé au chemin spécifié: " + documentsPath);
                return List.of();

            }
            System.out.println("Documents chargés avec succès: " + documents.size());
            return documents;
        }
        catch (Exception e) {
            System.err.println("Erreur lors de la résolution du chemin des documents: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la résolution du chemin des documents: " + e.getMessage(), e);
        }
    }
    
    /**
     * Initialise tous les agents spécialisés
     */
    private void initializeAgents() {
        System.out.println("📦 Initialisation des agents spécialisés...");
        
        // Agent Story - utilise le dossier story existant
        try {
            RagConfiguration configuration = new RagConfiguration("localhost", 5432, "postgres", "password", "postgres", "story_db");
            var serviceFactory = new ServiceFactory(configuration);
            agentRouter.registerAgent(AgentType.STORY, "story", serviceFactory);
        } catch (Exception e) {
            System.err.println("⚠️ Échec de l'initialisation de l'agent STORY: " + e.getMessage());
        }
        
        // Agent Math - utilise le dossier math existant
        try {
            RagConfiguration configuration = new RagConfiguration("localhost", 5432, "postgres", "password", "postgres", "math_db");
            var serviceFactory = new ServiceFactory(configuration);
            agentRouter.registerAgent(AgentType.MATH, "math", serviceFactory);
        } catch (Exception e) {
            System.err.println("⚠️ Échec de l'initialisation de l'agent MATH: " + e.getMessage());
        }
        
        // Agent Droit - utilise le nouveau dossier droit
        try {
            RagConfiguration configuration = new RagConfiguration("localhost", 5432, "postgres", "password", "postgres", "droit_db");
            var serviceFactory = new ServiceFactory(configuration);
            agentRouter.registerAgent(AgentType.DROIT, "droit", serviceFactory);
        } catch (Exception e) {
            System.err.println("⚠️ Échec de l'initialisation de l'agent DROIT: " + e.getMessage());
        }
        
        System.out.println("✅ Initialisation des agents terminée.");
        System.out.println(agentRouter.getAgentStats());
    }

    private UserInteractionService createAgentUserInteractionService(AgentRouter agentRouter) {
        return new AgentUserInteractionService(agentRouter);
    }
}
