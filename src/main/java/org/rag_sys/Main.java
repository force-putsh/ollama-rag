package org.rag_sys;

import org.rag_sys.config.RagConfiguration;
import org.rag_sys.orchestrator.RagSystemOrchestrator;

import java.net.URISyntaxException;

/**
 * Classe principale de l'application RAG
 * Principe SRP : Point d'entrée simple qui délègue la logique métier
 * Principe DIP : Dépend de l'orchestrateur, pas de l'implémentation
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Configuration par défaut
            RagConfiguration configuration = new RagConfiguration();
            
            // Créer et démarrer l'orchestrateur
            RagSystemOrchestrator orchestrator = new RagSystemOrchestrator(configuration);
            orchestrator.start();
            
        } catch (URISyntaxException e) {
            System.err.println("Erreur lors de la résolution du chemin des documents: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}