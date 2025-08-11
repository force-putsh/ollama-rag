package org.rag_sys.config;

/**
 * Configuration de l'application RAG
 * Principe SRP : Se concentre uniquement sur la configuration
 */
public class RagConfiguration {
    
    public static final String DEFAULT_MODEL_NAME = "mistral";
    public static final String DEFAULT_EMBEDDING_MODEL = "nomic-embed-text";
    public static final String DEFAULT_OLLAMA_BASE_URL = "http://localhost:11434";
    public static final String DEFAULT_POSTGRES_HOST = "localhost";
    public static final int DEFAULT_POSTGRES_PORT = 5432;
    public static final String DEFAULT_POSTGRES_USER = "postgres";
    public static final String DEFAULT_POSTGRES_PASSWORD = "password";
    public static final String DEFAULT_POSTGRES_DATABASE = "postgres";
    public static final String DEFAULT_POSTGRES_TABLE = "rag_embeddings";
    
    private final String modelName;
    private final String embeddingModel;
    private final String ollamaBaseUrl;
    private final String postgresHost;
    private final int postgresPort;
    private final String postgresUser;
    private final String postgresPassword;
    private final String postgresDatabase;
    private final String postgresTable;
    
    public RagConfiguration() {
        this(DEFAULT_MODEL_NAME, DEFAULT_EMBEDDING_MODEL, DEFAULT_OLLAMA_BASE_URL,
             DEFAULT_POSTGRES_HOST, DEFAULT_POSTGRES_PORT, DEFAULT_POSTGRES_USER,
             DEFAULT_POSTGRES_PASSWORD, DEFAULT_POSTGRES_DATABASE, DEFAULT_POSTGRES_TABLE);
    }
    
    public RagConfiguration(String modelName, String embeddingModel, String ollamaBaseUrl,
                           String postgresHost, int postgresPort, String postgresUser,
                           String postgresPassword, String postgresDatabase, String postgresTable) {
        this.modelName = modelName;
        this.embeddingModel = embeddingModel;
        this.ollamaBaseUrl = ollamaBaseUrl;
        this.postgresHost = postgresHost;
        this.postgresPort = postgresPort;
        this.postgresUser = postgresUser;
        this.postgresPassword = postgresPassword;
        this.postgresDatabase = postgresDatabase;
        this.postgresTable = postgresTable;
    }
    
    // Getters
    public String getModelName() { return modelName; }
    public String getEmbeddingModel() { return embeddingModel; }
    public String getOllamaBaseUrl() { return ollamaBaseUrl; }
    public String getPostgresHost() { return postgresHost; }
    public int getPostgresPort() { return postgresPort; }
    public String getPostgresUser() { return postgresUser; }
    public String getPostgresPassword() { return postgresPassword; }
    public String getPostgresDatabase() { return postgresDatabase; }
    public String getPostgresTable() { return postgresTable; }
}
