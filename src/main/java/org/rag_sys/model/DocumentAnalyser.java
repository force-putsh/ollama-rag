package org.rag_sys.model;


import dev.langchain4j.service.SystemMessage;


public interface DocumentAnalyser {
    @SystemMessage("You are a document analyser. Your task is to analyse documents and provide insights based on their content.")
    String analyse(String prompt);

    String promptTemplate = """
            You are a document analyser. Your task is to analyse documents and provide insights based on their content.
            The user will ask you questions about the documents.
            You will use the provided documents to answer the questions.
            Question: {{question}}
            Context: {{context}}
            Answer: "";
            """;
}
