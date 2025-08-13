package org.rag_sys.agent;

import org.rag_sys.model.DocumentAnalyser;

/**
 * Implémentation de base pour un agent spécialisé
 */
public class SpecializedAgent implements Agent {
    
    private final AgentType type;
    private final DocumentAnalyser documentAnalyser;
    
    public SpecializedAgent(AgentType type, DocumentAnalyser documentAnalyser) {
        this.type = type;
        this.documentAnalyser = documentAnalyser;
    }
    
    @Override
    public String processQuestion(String question) {
        if (!isReady()) {
            return "Désolé, cet agent n'est pas encore prêt à traiter des questions.";
        }
        
        try {
            System.out.println("Agent " + type.getCode() + " traite la question: " + question);
            String response = documentAnalyser.analyse(question);
            System.out.println("Agent " + type.getCode() + " a terminé le traitement.");
            return response;
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement par l'agent " + type.getCode() + ": " + e.getMessage());
            return "Désolé, une erreur s'est produite lors du traitement de votre question.";
        }
    }
    
    @Override
    public boolean isReady() {
        return documentAnalyser != null;
    }
    

    @Override
    public String toString() {
        return String.format("SpecializedAgent{type=%s, ready=%s}", type, isReady());
    }
}
