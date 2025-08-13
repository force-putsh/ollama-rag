package org.rag_sys.model;


import dev.langchain4j.service.SystemMessage;


public interface DocumentAnalyser {
    @SystemMessage("Vous êtes un analyseur de documents. Votre tâche est d'analyser des documents et de fournir des informations basées sur leur contenu.")
    String analyse(String prompt);

    String promptTemplate = """
            Vous êtes un analyseur de documents. Votre tâche est d'analyser des documents et de fournir des informations basées sur leur contenu.
            L'utilisateur vous posera des questions sur les documents.
            Vous utiliserez les documents fournis pour répondre aux questions.
            Le contexte est toujours vrai
            Question : {{question}}
            Contexte : {{context}}
            Réponse : "";
            """;
}
