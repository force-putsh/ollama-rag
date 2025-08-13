package org.rag_sys.services.impl;

import org.rag_sys.agent.AgentRouter;
import org.rag_sys.services.UserInteractionService;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.rag_sys.model.DocumentAnalyser;

import java.util.Scanner;

/**
 * Service d'interaction utilisateur avec système d'agents spécialisés
 */
public class AgentUserInteractionService implements UserInteractionService {
    
    private static final String EXIT_COMMAND_1 = "exit";
    private static final String EXIT_COMMAND_2 = "quit";
    private static final String STATS_COMMAND = "stats";
    private static final String HELP_COMMAND = "help";
    
    private final AgentRouter agentRouter;
    
    public AgentUserInteractionService(AgentRouter agentRouter) {
        this.agentRouter = agentRouter;
    }
    
    @Override
    public void startInteractiveSession(DocumentAnalyser documentAnalyser, EmbeddingStoreContentRetriever retriever) {
        printWelcomeMessage();
        
        Scanner scanner = new Scanner(System.in);
        
        do {
            System.out.print("\n🤖 Question: ");
            String input = scanner.nextLine();
            
            if (shouldExit(input)) {
                System.out.println("👋 Au revoir ! Merci d'avoir utilisé le système RAG avec agents spécialisés.");
                break;
            }
            
            if (input == null || input.trim().isEmpty()) {
                System.out.println("⚠️ Veuillez poser une question valide.");
                continue;
            }
            
            // Traitement des commandes spéciales
            if (input.equalsIgnoreCase(STATS_COMMAND)) {
                System.out.println(agentRouter.getAgentStats());
                continue;
            }
            
            if (input.equalsIgnoreCase(HELP_COMMAND)) {
                printHelpMessage();
                continue;
            }
            
            try {
                // Router la question vers l'agent approprié
                String response = agentRouter.routeQuestion(input);
                System.out.println("\n" + response);
                
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du traitement de la question: " + e.getMessage());
                e.printStackTrace();
            }
            
        } while (true);
        
        scanner.close();
    }
    
    private void printWelcomeMessage() {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════════╗
            ║         🚀 SYSTÈME RAG AVEC AGENTS SPÉCIALISÉS v2.0          ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                                                              ║
            ║  �️ GUARD  - Agent de garde intelligent qui analyse         ║
            ║              et route automatiquement vos questions         ║
            ║                                                              ║
            ║  📚 STORY  - Spécialiste des récits et histoires            ║
            ║              • Événements historiques                       ║
            ║              • Narratifs et biographies                     ║
            ║              • Chronologies et contextes                    ║
            ║                                                              ║
            ║  🔢 MATH   - Expert en mathématiques                        ║
            ║              • Algèbre et équations                         ║
            ║              • Géométrie et calculs                         ║
            ║              • Analyse et dérivation                        ║
            ║              • Probabilités et statistiques                 ║
            ║                                                              ║
            ║  ⚖️  DROIT  - Spécialiste juridique                         ║
            ║              • Droits fondamentaux                          ║
            ║              • Code civil et procédures                     ║
            ║              • Obligations et responsabilités               ║
            ║                                                              ║
            ║  💡 INTELLIGENCE AUTOMATIQUE :                               ║
            ║  L'agent de garde analyse automatiquement votre question    ║
            ║  et la dirige vers l'expert le plus approprié avec un       ║
            ║  score de confiance. Les questions inappropriées sont       ║
            ║  automatiquement rejetées pour votre sécurité.              ║
            ║                                                              ║
            ║  📝 COMMANDES SPÉCIALES :                                   ║
            ║  • 'stats' - Statistiques détaillées du système             ║
            ║  • 'help'  - Guide d'utilisation complet                    ║
            ║  • 'exit'  - Quitter l'application                          ║
            ║                                                              ║
            ╚══════════════════════════════════════════════════════════════╝
            
            🎯 Posez simplement votre question - l'agent de garde s'occupe du reste !
            """);
    }
    
    private void printHelpMessage() {
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════════╗
            ║                    📖 GUIDE D'UTILISATION                    ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                                                              ║
            ║  🛡️ COMMENT ÇA MARCHE :                                     ║
            ║                                                              ║
            ║  1. Vous posez votre question naturellement                 ║
            ║  2. L'agent de garde analyse votre question                 ║
            ║  3. Il attribue un score de confiance (0-100%)              ║
            ║  4. Si le score ≥ 30%, il route vers l'expert approprié     ║
            ║  5. L'expert spécialisé traite votre question               ║
            ║  6. Vous recevez une réponse détaillée et contextuelle      ║
            ║                                                              ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                    🎯 EXEMPLES DE QUESTIONS                  ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                                                              ║
            ║  📚 QUESTIONS HISTORIQUES :                                 ║
            ║  • "Raconte-moi les événements de 1942"                     ║
            ║  • "Que s'est-il passé pendant l'Opération Cyclone ?"      ║
            ║  • "Parle-moi des négociations de paix"                     ║
            ║                                                              ║
            ║  🔢 QUESTIONS MATHÉMATIQUES :                               ║
            ║  • "Comment résoudre l'équation x² - 5x + 6 = 0 ?"         ║
            ║  • "Calcule la dérivée de x² + 3x"                          ║
            ║  • "Quelle est l'aire d'un cercle de rayon 5 ?"             ║
            ║  • "Explique le théorème de Pythagore"                      ║
            ║                                                              ║
            ║  ⚖️ QUESTIONS JURIDIQUES :                                  ║
            ║  • "Quels sont mes droits fondamentaux ?"                   ║
            ║  • "Comment fonctionne un contrat ?"                        ║
            ║  • "Que dit la loi sur la propriété ?"                      ║
            ║  • "Explique la responsabilité civile"                      ║
            ║                                                              ║
            ║  🤖 QUESTIONS GÉNÉRALES :                                   ║
            ║  • "Qu'est-ce que l'intelligence artificielle ?"           ║
            ║  • "Comment fonctionne un ordinateur ?"                     ║
            ║  • "Explique-moi le concept de photosynthèse"               ║
            ║                                                              ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                      � REJETS AUTOMATIQUES                 ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                                                              ║
            ║  L'agent de garde rejette automatiquement :                 ║
            ║  • Questions inappropriées ou offensantes                   ║
            ║  • Demandes de contenu illégal ou nuisible                  ║
            ║  • Questions trop vagues ou incompréhensibles               ║
            ║  • Texte sans sens grammatical                              ║
            ║                                                              ║
            ║  💡 CONSEILS POUR DE MEILLEURES RÉPONSES :                  ║
            ║  • Soyez précis et clair dans vos questions                 ║
            ║  • Utilisez un langage respectueux et approprié             ║
            ║  • Donnez du contexte si votre question est complexe        ║
            ║  • N'hésitez pas à reformuler si nécessaire                 ║
            ║                                                              ║
            ╚══════════════════════════════════════════════════════════════╝
            """);
    }
    
    private boolean shouldExit(String input) {
        return input == null || 
               input.equalsIgnoreCase(EXIT_COMMAND_1) || 
               input.equalsIgnoreCase(EXIT_COMMAND_2);
    }
}
