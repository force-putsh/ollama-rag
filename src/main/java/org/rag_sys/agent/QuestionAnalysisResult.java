package org.rag_sys.agent;

/**
 * Résultat de l'analyse d'une question par l'agent guard
 */
public class QuestionAnalysisResult {
    private final AgentType recommendedAgent;
    private final double confidenceScore;
    private final String reasoning;
    private final boolean shouldProcess;

    public QuestionAnalysisResult(AgentType recommendedAgent, double confidenceScore, 
                                String reasoning, boolean shouldProcess) {
        this.recommendedAgent = recommendedAgent;
        this.confidenceScore = confidenceScore;
        this.reasoning = reasoning;
        this.shouldProcess = shouldProcess;
    }

    public AgentType getRecommendedAgent() {
        return recommendedAgent;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public String getReasoning() {
        return reasoning;
    }

    public boolean shouldProcess() {
        return shouldProcess;
    }

    @Override
    public String toString() {
        return String.format("QuestionAnalysisResult{agent=%s, score=%.2f, shouldProcess=%s, reasoning='%s'}", 
                           recommendedAgent, confidenceScore, shouldProcess, reasoning);
    }
}
