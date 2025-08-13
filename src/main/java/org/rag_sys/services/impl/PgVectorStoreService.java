package org.rag_sys.services.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.rag_sys.model.DbVectorModel;
import org.rag_sys.services.VectorStoreService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du service de vector store utilisant PgVector
 * Principe SRP : Se concentre uniquement sur la gestion du vector store
 * Principe OCP : Peut être étendu pour supporter d'autres types de stores
 */
public class PgVectorStoreService implements VectorStoreService {
    private static final int CHUNK_SIZE = 1000;
    private static final int OVERLAP = 200;
    
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;
    private final String table;

    
    public PgVectorStoreService(DbVectorModel dbVectorModel) {
        this.host = dbVectorModel.getDbHost();
        this.port = dbVectorModel.getDbPort();
        this.user = dbVectorModel.getDbUser();
        this.password = dbVectorModel.getDbPassword();
        this.database = dbVectorModel.getDbName();
        this.table = dbVectorModel.getDbTable();
    }
    
    @Override
    public EmbeddingStore<TextSegment> createVectorStore(List<Document> documents, EmbeddingModel embeddingModel) {
        DockerImageName dockerImageName = DockerImageName.parse("pgvector/pgvector:pg17");
        
        try (var pgVectorContainer = new PostgreSQLContainer<>(dockerImageName)) {
            // Créer les segments de texte à partir des documents
            List<TextSegment> textSegments = new ArrayList<>();
            
            for (Document document : documents) {
                TextSegment textSegment = document.toTextSegment();
                textSegments.add(textSegment);
            }
            
            // Créer le store PgVector
            EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                    .host(host)
                    .port(port)
                    .user(user)
                    .password(password)
                    .table(table)
                    .database(database)
                    .dimension(embeddingModel.dimension())
                    .build();
            
            // Configurer l'ingesteur
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(DocumentSplitters.recursive(CHUNK_SIZE, OVERLAP))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();
            
            ingestor.ingest(documents);
            
            return embeddingStore;
        }
    }
}
