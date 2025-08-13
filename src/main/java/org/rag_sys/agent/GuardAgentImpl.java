package org.rag_sys.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Implémentation de l'agent de garde
 */
public class GuardAgentImpl {
    
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.3;
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.7;
    private final GuardAgent guardAgent;
    private final ObjectMapper objectMapper;
    
    public GuardAgentImpl(String modelName, String baseUrl) {
        // Créer le modèle de chat pour l'agent de garde
        OllamaChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.1) // Température basse pour plus de cohérence dans l'analyse
                .timeout(java.time.Duration.ofSeconds(30))
                .build();
        
        // Créer l'agent de garde avec AI Services
        this.guardAgent = AiServices.builder(GuardAgent.class)
                .chatModel(chatModel)
                .build();
        
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Analyse une question et détermine quel agent doit la traiter
     */
    public QuestionAnalysisResult analyzeQuestion(String question) {
        try {
            // Validation préliminaire
//            if (question == null || question.trim().isEmpty()) {
//                return new QuestionAnalysisResult(
//                    AgentType.GUARD,
//                    0.0,
//                    "Question vide ou nulle",
//                    false
//                );
//            }
            
            // Nettoyer et préprarer la question
            String cleanedQuestion = question.trim();
            System.out.println("🛡️ Agent de garde analyse la question: " + cleanedQuestion);
            
            // Obtenir l'analyse de l'agent de garde
            String jsonResponse = guardAgent.analyzeQuestion(cleanedQuestion);
            System.out.println("📋 Réponse brute de l'agent de garde: " + jsonResponse);
            
            // Parser la réponse JSON
            QuestionAnalysisResult result = parseAnalysisResult(jsonResponse, cleanedQuestion);
            
            // Validation et ajustement final
            result = validateAndAdjustResult(result, cleanedQuestion);
            
            System.out.println("✅ Analyse finale: " + result);
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'analyse par l'agent de garde: " + e.getMessage());
            e.printStackTrace();
            
            // Retourner un résultat de fallback sécurisé
            return createFallbackAnalysis(question, e);
        }
    }
    
    private QuestionAnalysisResult parseAnalysisResult(String jsonResponse, String originalQuestion) {
        try {
            // Nettoyer la réponse de tous les artifacts possibles
            String cleanJson = cleanJsonResponse(jsonResponse);
            
            // Parser le JSON
            var jsonNode = objectMapper.readTree(cleanJson);
            
            // Extraire les champs avec validation
            String agentTypeStr = extractStringField(jsonNode, "recommendedAgent", "GUARD");
            double confidenceScore = extractDoubleField(jsonNode, "confidenceScore", 0.2);
            String reasoning = extractStringField(jsonNode, "reasoning", "Analyse par défaut");
            boolean shouldProcessFromJson = extractBooleanField(jsonNode, "shouldProcess", false);
            
            // Validation des valeurs
            confidenceScore = Math.max(0.0, Math.min(1.0, confidenceScore));
            AgentType agentType = AgentType.fromCode(agentTypeStr);
            
            // Appliquer notre seuil de confiance
            boolean shouldProcess = shouldProcessFromJson && confidenceScore >= MIN_CONFIDENCE_THRESHOLD;
            
            return new QuestionAnalysisResult(agentType, confidenceScore, reasoning, shouldProcess);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du parsing de la réponse JSON: " + e.getMessage());
            System.err.println("JSON reçu: " + jsonResponse);
            
            // Analyse de fallback basique
            return performBasicAnalysis(originalQuestion);
        }
    }
    
    private String cleanJsonResponse(String jsonResponse) {
        if (jsonResponse == null) return "{}";
        
        String cleaned = jsonResponse.trim();
        
        // Supprimer les blocs de code markdown
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        // Extraire le JSON s'il y a du texte avant/après
        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        
        if (firstBrace != -1 && lastBrace != -1 && firstBrace <= lastBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }
        
        return cleaned.trim();
    }
    
    private String extractStringField(com.fasterxml.jackson.databind.JsonNode jsonNode, String fieldName, String defaultValue) {
        var field = jsonNode.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : defaultValue;
    }
    
    private double extractDoubleField(com.fasterxml.jackson.databind.JsonNode jsonNode, String fieldName, double defaultValue) {
        var field = jsonNode.get(fieldName);
        return (field != null && !field.isNull()) ? field.asDouble() : defaultValue;
    }
    
    private boolean extractBooleanField(com.fasterxml.jackson.databind.JsonNode jsonNode, String fieldName, boolean defaultValue) {
        var field = jsonNode.get(fieldName);
        return (field != null && !field.isNull()) ? field.asBoolean() : defaultValue;
    }
    
    private QuestionAnalysisResult validateAndAdjustResult(QuestionAnalysisResult result, String question) {
        // Vérifier si la question contient des mots-clés offensants
        if (containsInappropriateContent(question)) {
            return new QuestionAnalysisResult(
                AgentType.GUARD,
                0.0, 
                "Question rejetée: contenu inapproprié détecté", 
                false
            );
        }
        
        // Ajuster le score de confiance si nécessaire
        double adjustedScore = result.getConfidenceScore();
        String adjustedReasoning = result.getReasoning();
        
        // Si la question est très courte et vague
        if (question.length() < 10) {
            adjustedScore = Math.min(adjustedScore, 0.4);
            adjustedReasoning += " (Score ajusté: question très courte)";
        }
        
        // Si le score est très élevé, on peut être plus confiant
        if (adjustedScore >= HIGH_CONFIDENCE_THRESHOLD) {
            adjustedReasoning += " (Haute confiance)";
        }
        
        return new QuestionAnalysisResult(
            result.getRecommendedAgent(),
            adjustedScore,
            adjustedReasoning,
            result.shouldProcess() && adjustedScore >= MIN_CONFIDENCE_THRESHOLD
        );
    }
    
    private boolean containsInappropriateContent(String question) {
        String lowercaseQuestion = question.toLowerCase();
        String[] inappropriateWords = {"hack", "pirate", "illegal", "violence", "haine"};
        
        for (String word : inappropriateWords) {
            if (lowercaseQuestion.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    private QuestionAnalysisResult createFallbackAnalysis(String question, Exception error) {
        if (question == null || question.trim().isEmpty()) {
            return new QuestionAnalysisResult(
                AgentType.GUARD,
                0.0, 
                "Question vide", 
                false
            );
        }
        
        // Analyse de base très simple
        QuestionAnalysisResult basicResult = performBasicAnalysis(question);
        
        return new QuestionAnalysisResult(
            basicResult.getRecommendedAgent(),
            Math.min(basicResult.getConfidenceScore(), 0.4), // Limiter la confiance en cas d'erreur
            "Analyse de fallback: " + basicResult.getReasoning() + " (Erreur: " + error.getMessage() + ")",
            basicResult.getConfidenceScore() >= MIN_CONFIDENCE_THRESHOLD
        );
    }
    
    private QuestionAnalysisResult performBasicAnalysis(String question) {
        if (question == null || question.trim().isEmpty()) {
            return new QuestionAnalysisResult(AgentType.GUARD, 0.0,
                "Question vide ou nulle", false);
        }
        
        String lowerQuestion = question.toLowerCase();
        
        // Analyse basique par mots-clés avec scores plus précis
        if (containsMathKeywords(lowerQuestion)) {
            return new QuestionAnalysisResult(AgentType.MATH, 0.6, 
                "Analyse basique: détection de mots-clés mathématiques", true);
        }
        
        if (containsStoryKeywords(lowerQuestion)) {
            return new QuestionAnalysisResult(AgentType.STORY, 0.6, 
                "Analyse basique: détection de mots-clés liés aux histoires", true);
        }
        
        if (containsLegalKeywords(lowerQuestion)) {
            return new QuestionAnalysisResult(AgentType.DROIT, 0.6, 
                "Analyse basique: détection de mots-clés juridiques", true);
        }
        
        // Question générale avec score modéré
        return new QuestionAnalysisResult(AgentType.GUARD, 0.4,
            "Analyse basique: question générale", true);
    }
    
    private boolean containsMathKeywords(String question) {
        String[] mathKeywords = {
            "math", "calcul", "équation", "nombre", "addition", "soustraction", 
            "multiplication", "division", "dérivée", "intégrale", "fonction",
            "algèbre", "géométrie", "statistique", "probabilité", "résoudre",
            "x²", "√", "+", "-", "×", "÷", "=", "%"
        };
        
        return containsAnyKeyword(question, mathKeywords);
    }
    
    private boolean containsStoryKeywords(String question) {
        String[] storyKeywords = {
            "histoire", "récit", "événement", "narratif", "raconte", "guerre",
            "bataille", "biographie", "personnage", "époque", "siècle",
            "révolution", "empire", "roi", "reine", "président", "leader",
            "1942", "1943", "1944", "1945", "ww2", "seconde guerre"
        };
        
        return containsAnyKeyword(question, storyKeywords);
    }
    
    private boolean containsLegalKeywords(String question) {
        String[] legalKeywords = {
            "droit", "loi", "juridique", "légal", "contrat", "tribunal",
            "justice", "avocat", "juge", "procédure", "code civil",
            "constitution", "article", "réglementation", "sanction",
            "propriété", "responsabilité", "obligation"
        };
        
        return containsAnyKeyword(question, legalKeywords);
    }
    
    private boolean containsAnyKeyword(String question, String[] keywords) {
        for (String keyword : keywords) {
            if (question.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
