package org.rag_sys.services.impl;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.rag_sys.model.DocumentAnalyser;
import org.rag_sys.services.UserInteractionService;

import java.util.Map;
import java.util.Scanner;

/**
 * Implémentation du service d'interaction utilisateur via console
 */
public class ConsoleUserInteractionService implements UserInteractionService {
    
    private static final String EXIT_COMMAND_1 = "exit";
    private static final String EXIT_COMMAND_2 = "quit";
    
    @Override
    public void startInteractiveSession(DocumentAnalyser documentAnalyser, EmbeddingStoreContentRetriever retriever) {
        System.out.println("Pour utiliser le système RAG, vous pouvez maintenant poser des questions sur les documents.");
        System.out.println("Tapez votre question et appuyez sur Entrée. Tapez 'exit' ou 'quit' pour quitter l'application.");
        
        Scanner scanner = new Scanner(System.in);
        
        do {
            // Lire l'entrée utilisateur
            System.out.print("Question: ");
            String question = scanner.nextLine();
            
            if (shouldExit(question)) {
                System.out.println("Sortie de l'application.");
                break;
            }
            
            if (question == null || question.trim().isEmpty()) {
                System.out.println("Veuillez poser une question valide.");
                continue;
            }
            
            try {
                // Créer un prompt en utilisant la question
                Prompt prompt = PromptTemplate.from(DocumentAnalyser.promptTemplate).apply(Map.of(
                        "question", question,
                        "context", retriever
                ));
                String response = documentAnalyser.analyse(question);
                System.out.println("Réponse: " + response);
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement de la question: " + e.getMessage());
            }
            
        } while (true);
        
        scanner.close();
    }
    
    private boolean shouldExit(String input) {
        return input == null || 
               input.equalsIgnoreCase(EXIT_COMMAND_1) || 
               input.equalsIgnoreCase(EXIT_COMMAND_2);
    }
}
