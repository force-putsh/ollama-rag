# Système RAG Refactorisé avec Principes SOLID

## Architecture

Cette application a été refactorisée en appliquant les principes SOLID pour une meilleure maintenabilité, extensibilité et testabilité.

### Principes SOLID Appliqués

#### 1. Single Responsibility Principle (SRP)
Chaque classe a une responsabilité unique :
- `DocumentLoaderService` : Chargement des documents
- `EmbeddingModelService` : Création des modèles d'embedding
- `VectorStoreService` : Gestion du vector store
- `RagService` : Configuration de la chaîne RAG
- `UserInteractionService` : Interaction avec l'utilisateur

#### 2. Open/Closed Principle (OCP)
Les services sont ouverts à l'extension, fermés à la modification :
- Nouvelles implémentations peuvent être ajoutées sans modifier le code existant
- Exemple : `OpenAIEmbeddingModelService` peut être ajouté facilement

#### 3. Liskov Substitution Principle (LSP)
Les implémentations peuvent être substituées par leurs interfaces :
- Toutes les implémentations respectent les contrats de leurs interfaces

#### 4. Interface Segregation Principle (ISP)
Interfaces spécifiques et cohésives :
- Chaque interface a un rôle bien défini
- Pas de méthodes inutiles dans les interfaces

#### 5. Dependency Inversion Principle (DIP)
Dépendance sur les abstractions, pas les implémentations :
- `RagSystemOrchestrator` dépend des interfaces, pas des classes concrètes
- `ServiceFactory` gère l'injection de dépendances

## Structure du Projet

```
src/main/java/org/rag_sys/
├── Main.java                          # Point d'entrée simple
├── config/
│   └── RagConfiguration.java          # Configuration centralisée
├── factory/
│   └── ServiceFactory.java            # Factory pour créer les services
├── model/
│   └── DocumentAnalyser.java          # Interface d'analyse des documents
├── orchestrator/
│   └── RagSystemOrchestrator.java     # Coordinateur principal
└── services/
    ├── DocumentLoaderService.java     # Interface chargement documents
    ├── EmbeddingModelService.java     # Interface modèles embedding
    ├── RagService.java                # Interface service RAG
    ├── UserInteractionService.java    # Interface interaction utilisateur
    ├── VectorStoreService.java        # Interface vector store
    └── impl/
        ├── ConsoleUserInteractionService.java    # Implémentation console
        ├── FileSystemDocumentLoaderService.java  # Implémentation filesystem
        ├── OllamaEmbeddingModelService.java      # Implémentation Ollama
        ├── OllamaRagService.java                 # Implémentation RAG Ollama
        └── PgVectorStoreService.java             # Implémentation PgVector
```

## Avantages de la Refactorisation

### 1. Testabilité
- Chaque service peut être testé indépendamment
- Possibilité de mocker les dépendances facilement

### 2. Maintenabilité
- Code plus lisible et organisé
- Responsabilités clairement séparées
- Modifications localisées

### 3. Extensibilité
- Ajout facile de nouvelles implémentations
- Support de nouveaux providers (OpenAI, Cohere, etc.)
- Nouvelles interfaces utilisateur (web, GUI)

### 4. Configuration
- Configuration centralisée dans `RagConfiguration`
- Paramètres modifiables sans changer le code

## Exemples d'Extensions

### Nouveau Provider d'Embedding
```java
public class OpenAIEmbeddingModelService implements EmbeddingModelService {
    @Override
    public EmbeddingModel createEmbeddingModel(String modelName) {
        // Implémentation OpenAI
    }
}
```

### Nouvelle Interface Utilisateur
```java
public class WebUserInteractionService implements UserInteractionService {
    @Override
    public void startInteractiveSession(DocumentAnalyser documentAnalyser) {
        // Implémentation web
    }
}
```

### Nouveau Vector Store
```java
public class ChromaVectorStoreService implements VectorStoreService {
    @Override
    public EmbeddingStore<TextSegment> createVectorStore(List<Document> documents, EmbeddingModel embeddingModel) {
        // Implémentation Chroma
    }
}
```

## Utilisation

L'application fonctionne de la même manière qu'avant, mais avec une architecture beaucoup plus propre et maintenir :

```bash
mvn compile exec:java -Dexec.mainClass="org.rag_sys.Main"
```

## Tests

La nouvelle architecture facilite grandement l'écriture de tests unitaires :

```java
@Test
public void testDocumentLoading() {
    DocumentLoaderService service = new FileSystemDocumentLoaderService();
    List<Document> documents = service.loadDocuments("/path/to/docs");
    assertFalse(documents.isEmpty());
}
```
