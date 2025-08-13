package org.rag_sys.agent;

import org.rag_sys.config.RagConfiguration;
import org.rag_sys.factory.ServiceFactory;
import org.rag_sys.model.DocumentAnalyser;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Routeur d'agents qui gère la distribution des questions aux agents spécialisés
 * avec métriques de performance et gestion d'erreurs améliorée
 */
public class AgentRouter {
    
    private final Map<AgentType, Agent> agents;
    private final GuardAgentImpl guardAgent;
    private final RagConfiguration configuration;
    
    // Métriques de performance
    private final AtomicInteger totalQuestions = new AtomicInteger(0);
    private final AtomicInteger rejectedQuestions = new AtomicInteger(0);
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    private final Map<AgentType, AtomicInteger> agentUsageCount = new HashMap<>();
    private LocalDateTime startTime;
    
    public AgentRouter(RagConfiguration configuration) {
        this.configuration = configuration;
        this.agents = new HashMap<>();
        this.guardAgent = new GuardAgentImpl(configuration.getModelName(), configuration.getOllamaBaseUrl());
        this.startTime = LocalDateTime.now();
        
        // Initialiser les compteurs d'usage pour chaque type d'agent
        for (AgentType type : AgentType.values()) {
            agentUsageCount.put(type, new AtomicInteger(0));
        }
        
        System.out.println("🔧 AgentRouter initialisé avec l'agent de garde");
    }

    /**
     * Enregistre un agent spécialisé
     */
    private void registerAgent(AgentType type, Agent agent) {
        agents.put(type, agent);
        System.out.println("Agent " + type.getCode() + " enregistré avec succès.");
    }
    
    /**
     * Enregistre un agent en créant automatiquement sa chaîne RAG
     */
    public void registerAgent(AgentType type, String documentDirectory, ServiceFactory serviceFactory) {
        try {
            System.out.println("Création de l'agent " + type.getCode() + " avec le répertoire: " + documentDirectory);
            
            DocumentAnalyser documentAnalyser = createAgentDocumentAnalyser(documentDirectory, serviceFactory);
            
            if (documentAnalyser != null) {
                Agent agent = new SpecializedAgent(type, documentAnalyser);
                registerAgent(type, agent);
            } else {
                System.err.println("Échec de la création de l'agent " + type.getCode());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de l'agent " + type.getCode() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Route une question vers l'agent approprié avec métriques de performance
     */
    public String routeQuestion(String question) {
        long startTime = System.currentTimeMillis();
        totalQuestions.incrementAndGet();
        
        try {
            // 1. Analyser la question avec l'agent de garde
            QuestionAnalysisResult analysis = guardAgent.analyzeQuestion(question);
            System.out.println("📊 Résultat de l'analyse: " + analysis);
            
            // 2. Vérifier si la question doit être traitée
            if (!analysis.shouldProcess()) {
                rejectedQuestions.incrementAndGet();
                return formatRejectionResponse(analysis);
            }
            
            // 3. Obtenir l'agent recommandé
            AgentType recommendedType = analysis.getRecommendedAgent();
            Agent recommendedAgent = agents.get(recommendedType);
            
            if (recommendedAgent == null || !recommendedAgent.isReady()) {
                return formatAgentUnavailableResponse(recommendedType);
            }
            
            // 4. Incrémenter les statistiques d'usage
            agentUsageCount.get(recommendedType).incrementAndGet();
            
            // 5. Traiter la question avec l'agent spécialisé
            System.out.println("🚀 Routage vers l'agent " + recommendedType.getCode().toUpperCase());
            String response = recommendedAgent.processQuestion(question);
            
            // 6. Formater la réponse avec des informations de contexte
            return formatSuccessResponse(analysis, response);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du routage de la question: " + e.getMessage());
            e.printStackTrace();
            return formatErrorResponse(e);
        } finally {
            // Enregistrer le temps de traitement
            long processingTime = System.currentTimeMillis() - startTime;
            totalProcessingTime.addAndGet(processingTime);
            System.out.println("⏱️ Temps de traitement: " + processingTime + "ms");
        }
    }
    
    /**
     * Retourne les statistiques détaillées des agents et de performance
     */
    public String getAgentStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("╔══════════════════════════════════════════════════════════════╗\n");
        stats.append("║                    📊 STATISTIQUES DÉTAILLÉES                ║\n");
        stats.append("╠══════════════════════════════════════════════════════════════╣\n");
        
        // Statistiques générales
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        stats.append(String.format("║ 🕐 Démarrage système: %s     ║\n", startTime.format(formatter)));
        stats.append(String.format("║ 📝 Questions traitées: %-5d                           ║\n", totalQuestions.get()));
        stats.append(String.format("║ ❌ Questions rejetées: %-5d                            ║\n", rejectedQuestions.get()));
        
        if (totalQuestions.get() > 0) {
            double rejectionRate = (double) rejectedQuestions.get() / totalQuestions.get() * 100;
            double avgProcessingTime = (double) totalProcessingTime.get() / totalQuestions.get();
            stats.append(String.format("║ 📊 Taux de rejet: %.1f%%                              ║\n", rejectionRate));
            stats.append(String.format("║ ⚡ Temps moyen: %.0f ms                              ║\n", avgProcessingTime));
        }
        
        stats.append("╠══════════════════════════════════════════════════════════════╣\n");
        stats.append("║                        🤖 ÉTAT DES AGENTS                    ║\n");
        stats.append("╠══════════════════════════════════════════════════════════════╣\n");
        
        // Statistiques des agents
        for (AgentType type : AgentType.values()) {
            if (type == AgentType.GUARD) continue; // Skip l'agent de garde dans cette liste
            
            Agent agent = agents.get(type);
            int usage = agentUsageCount.get(type).get();
            
            if (agent != null) {
                String status = agent.isReady() ? "✅ Actif" : "⚠️ Indisponible";
                stats.append(String.format("║ %s %-6s: %s (Utilisé: %-3d fois)         ║\n", 
                    getAgentEmoji(type), type.getCode().toUpperCase(), status, usage));
            } else {
                stats.append(String.format("║ %s %-6s: ❌ Non enregistré                    ║\n", 
                    getAgentEmoji(type), type.getCode().toUpperCase()));
            }
        }
        
        stats.append("╚══════════════════════════════════════════════════════════════╝\n");
        
        return stats.toString();
    }
    
    private String getAgentEmoji(AgentType type) {
        return switch (type) {
            case STORY -> "📚";
            case MATH -> "🔢";
            case DROIT -> "⚖️";
//            case GENERAL -> "🤖";
            case GUARD -> "🛡️";
        };
    }
    
    private DocumentAnalyser createAgentDocumentAnalyser(String documentDirectory, ServiceFactory serviceFactory) {
        try {
            return new org.rag_sys.orchestrator.RagSystemOrchestrator(configuration)
                    .registerRagChainForDifferentAgents(documentDirectory, serviceFactory, configuration);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'analyseur de documents: " + e.getMessage());
            return null;
        }
    }
    
    private String formatRejectionResponse(QuestionAnalysisResult analysis) {
        return String.format("""
            🚫 **Question Rejetée par l'Agent de Garde**
            
            ❌ **Raison du rejet :**
            %s
            
            📊 **Score de confiance :** %.2f/1.0 (Seuil minimum: 0.30)
            
            💡 **Suggestions :**
            • Reformulez votre question de manière plus claire et précise
            • Assurez-vous que votre question est appropriée et respectueuse
            • Vérifiez que votre question entre dans l'un de nos domaines d'expertise :
              📚 Histoires et récits  🔢 Mathématiques  ⚖️ Droit  🤖 Questions générales
            
            ❓ Tapez 'help' pour plus d'informations sur l'utilisation du système.
            """, analysis.getReasoning(), analysis.getConfidenceScore());
    }
    
    private String formatAgentUnavailableResponse(AgentType agentType) {
        return String.format("""
            ⚠️ **Agent Temporairement Indisponible**
            
            L'agent spécialisé **%s** (%s) n'est pas disponible actuellement.
            
            🔧 **Causes possibles :**
            • Agent en cours d'initialisation
            • Problème de connexion avec la base de données
            • Erreur lors du chargement des documents
            
            🔄 **Solutions :**
            • Réessayez dans quelques instants
            • Reformulez votre question pour qu'elle soit traitée par un autre agent
            • Contactez l'administrateur si le problème persiste
            
            📊 Tapez 'stats' pour voir l'état de tous les agents.
            """, 
            agentType.getCode().toUpperCase(), 
            agentType.getDescription());
    }
    
    private String formatErrorResponse(Exception error) {
        return String.format("""
            💥 **Erreur Système**
            
            Une erreur inattendue s'est produite lors du traitement de votre question.
            
            🔍 **Détails techniques :**
            %s
            
            🛠️ **Actions recommandées :**
            • Réessayez votre question
            • Vérifiez que votre question est bien formée
            • Si le problème persiste, contactez l'administrateur
            
            📝 L'erreur a été enregistrée pour analyse.
            """, error.getMessage());
    }
    
    private String formatSuccessResponse(QuestionAnalysisResult analysis, String response) {
        return String.format("""
            🤖 **Réponse de l'Agent %s** (Confiance: %.1f%%)
            
            %s
            
            ---
            💡 *Cette réponse a été générée par l'agent spécialisé en %s*
            """, 
            analysis.getRecommendedAgent().getCode().toUpperCase(),
            analysis.getConfidenceScore() * 100,
            response,
            analysis.getRecommendedAgent().getDescription().toLowerCase());
    }
}
