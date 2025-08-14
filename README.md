# 🚀 Système RAG avec Agents Spécialisés

Un système de Retrieval-Augmented Generation (RAG) intelligent utilisant des agents spécialisés pour traiter différents domaines de connaissances. Le système utilise Ollama pour l'inférence des modèles, PostgreSQL avec pgvector pour le stockage vectoriel, et LangChain4j pour l'orchestration.

## 🎯 Fonctionnalités

- **🛡️ Agent de Garde Intelligent** : Analyse et route automatiquement les questions vers l'agent approprié
- **📚 Agent Story** : Spécialisé dans l'analyse de récits historiques et d'événements de la Seconde Guerre mondiale
- **🔢 Agent Math** : Expert en mathématiques (algèbre, géométrie, analyse, probabilités)
- **⚖️ Agent Droit** : Spécialiste juridique (droits fondamentaux, code civil)
- **💾 Stockage Vectoriel** : Base de données PostgreSQL avec extension pgvector pour les embeddings
- **🔍 Retrieval Intelligent** : Recherche sémantique avancée dans les documents
- **📊 Statistiques en Temps Réel** : Suivi des performances et de l'utilisation des agents

## 🏗️ Architecture

```
ollama-rag/
├── src/main/java/org/rag_sys/
│   ├── Main.java                           # Point d'entrée de l'application
│   ├── agent/                              # Agents spécialisés
│   │   ├── AgentType.java                  # Énumération des types d'agents
│   │   ├── GuardAgent.java                 # Interface de l'agent de garde
│   │   ├── GuardAgentImpl.java             # Implémentation de l'agent de garde
│   │   └── AgentRouter.java                # Routeur d'agents
│   ├── config/                             # Configuration
│   │   └── RagConfiguration.java           # Configuration du système
│   ├── orchestrator/                       # Orchestration
│   │   └── RagSystemOrchestrator.java      # Orchestrateur principal
│   ├── services/                           # Services métier
│   │   ├── UserInteractionService.java     # Interface d'interaction utilisateur
│   │   └── impl/                           # Implémentations des services
│   └── factory/                            # Factories pour l'injection de dépendances
└── src/main/resources/                     # Documents de connaissances
    ├── droit/                              # Documents juridiques
    ├── math/                               # Documents mathématiques
    └── story/                              # Récits historiques WW2
```

## 🚀 Get Started

### Prérequis

- **Java 21** ou supérieur
- **Maven 3.8+** pour la gestion des dépendances
- **Docker** et **Docker Compose** pour les services externes
- **Git** pour le clonage du repository

### Installation

#### 1. Cloner le repository

```bash
git clone https://github.com/votre-username/ollama-rag.git
cd ollama-rag
```

#### 2. Configurer l'environnement

Créez un fichier `docker-compose.yml` dans le répertoire racine :

```yaml
version: '3.8'

services:
  # Service Ollama pour les modèles LLM
  ollama:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama-data:/root/.ollama
    environment:
      - OLLAMA_ORIGINS=*
    command: serve

  # Base de données PostgreSQL avec extension pgvector
  postgres:
    image: pgvector/pgvector:pg16
    environment:
      POSTGRES_DB: rag_db
      POSTGRES_USER: rag_user
      POSTGRES_PASSWORD: rag_password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  ollama-data:
  postgres-data:
```

Créez le fichier `init.sql` pour initialiser la base de données :



#### 3. Démarrer les services

```bash
# Démarrer PostgreSQL et Ollama
docker-compose up -d

# Vérifier que les services sont actifs
docker-compose ps
```

#### 4. Configurer Ollama

```bash
# Télécharger le modèle principal (llama3.1:8b recommandé)
docker exec -it ollama-rag-ollama-1 ollama pull llama3.1:8b

# Optionnel : autres modèles selon vos besoins
docker exec -it ollama-rag-ollama-1 ollama pull mistral:7b
docker exec -it ollama-rag-ollama-1 ollama pull codellama:7b
```

#### 5. Compiler et lancer l'application

```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn exec:java -Dexec.mainClass="org.rag_sys.Main"
```

### Configuration Avancée


#### Configuration dans le Code

Modifiez `RagConfiguration.java` pour personnaliser les paramètres :

```java
public class RagConfiguration {
    // Valeurs par défaut configurables
    private static final String DEFAULT_MODEL_NAME = "llama3.1:8b";
    private static final String DEFAULT_OLLAMA_BASE_URL = "http://localhost:11434";
    // ... autres configurations
}
```

### Utilisation

#### Interface Interactive

L'application démarre avec une interface console interactive :

```
╔══════════════════════════════════════════════════════════════╗
║         🚀 SYSTÈME RAG AVEC AGENTS SPÉCIALISÉS v2.0          ║
╠══════════════════════════════════════════════════════════════╣
║  🛡️ GUARD  - Agent de garde intelligent                      ║
║  📚 STORY  - Spécialiste des récits et histoires             ║
║  🔢 MATH   - Expert en mathématiques                         ║
║  ⚖️ DROIT  - Spécialiste juridique                           ║
╚══════════════════════════════════════════════════════════════╝

🤖 Question: 
```

#### Commandes Disponibles

- **Questions normales** : Posez simplement votre question
- `stats` : Affiche les statistiques d'utilisation des agents
- `help` : Guide d'utilisation détaillé
- `exit` ou `quit` : Quitte l'application

#### Exemples d'Utilisation

```bash
# Questions mathématiques
🤖 Question: Résoudre l'équation x² + 5x + 6 = 0

# Questions historiques
🤖 Question: Raconte-moi le débarquement en Normandie

# Questions juridiques
🤖 Question: Quels sont les droits fondamentaux en France ?

# Statistiques du système
🤖 Question: stats
```

## 🤝 Contribution

### Standards de Développement

Ce projet suit les principes SOLID et les bonnes pratiques Java :

- **Single Responsibility Principle (SRP)** : Chaque classe a une responsabilité unique
- **Open/Closed Principle (OCP)** : Extensions sans modifications
- **Liskov Substitution Principle (LSP)** : Substitution des interfaces
- **Interface Segregation Principle (ISP)** : Interfaces spécialisées
- **Dependency Inversion Principle (DIP)** : Injection de dépendances

### Architecture du Code

#### Structure des Packages

```
org.rag_sys/
├── agent/          # Agents spécialisés et routage
├── config/         # Configuration centralisée
├── factory/        # Factories pour l'injection de dépendances
├── model/          # Modèles de données
├── orchestrator/   # Orchestration du système
└── services/       # Services métier
    └── impl/       # Implémentations concrètes
```

#### Patterns Utilisés

- **Factory Pattern** : Création des services et composants
- **Strategy Pattern** : Agents spécialisés interchangeables
- **Builder Pattern** : Configuration des modèles Ollama
- **Service Layer** : Séparation des responsabilités métier

### Guide de Contribution

#### 1. Fork et Clone

```bash
# Fork le repository sur GitHub
# Puis cloner votre fork
git clone https://github.com/votre-username/ollama-rag.git
cd ollama-rag
```

#### 2. Créer une Branche

```bash
# Créer une branche pour votre fonctionnalité
git checkout -b feature/nom-de-votre-feature

# Ou pour un bugfix
git checkout -b bugfix/description-du-bug
```

#### 3. Développement

##### Ajouter un Nouvel Agent

Pour ajouter un agent spécialisé :

1. **Définir le type dans `AgentType.java`** :
```java
public enum AgentType {
    // ... agents existants
    SCIENCE("science", "Agent spécialisé en sciences");
}
```

2. **Créer l'interface de l'agent** :
```java
public interface ScienceAgent {
    @SystemMessage("Tu es un expert en sciences...")
    String analyzeScientificQuestion(String question, List<String> context);
}
```

3. **Implémenter l'agent** :
```java
public class ScienceAgentImpl {
    // Implémentation similaire aux autres agents
}
```

4. **Ajouter les documents** dans `src/main/resources/science/`

5. **Mettre à jour le routeur** dans `AgentRouter.java`

##### Ajouter de Nouveaux Documents

```bash
# Structure recommandée pour les nouveaux domaines
src/main/resources/nouveau-domaine/
├── document1.txt
├── document2.txt
└── README.md  # Description du contenu
```

#### 4. Tests

```bash
# Compiler et vérifier
mvn clean compile

# Tester manuellement
mvn exec:java -Dexec.mainClass="org.rag_sys.Main"
```

#### 5. Convention de Code

##### Style Java

- **Indentation** : 4 espaces
- **Noms de classes** : PascalCase (`AgentRouter`)
- **Noms de méthodes** : camelCase (`analyzeQuestion`)
- **Constantes** : UPPER_SNAKE_CASE (`DEFAULT_MODEL_NAME`)

##### Documentation

- **Javadoc** obligatoire pour les classes publiques
- **Commentaires** pour la logique complexe
- **README** pour les nouveaux modules

Exemple de documentation :

```java
/**
 * Service d'interaction utilisateur avec système d'agents spécialisés
 * 
 * @author Votre Nom
 * @since 2.0
 */
public class AgentUserInteractionService implements UserInteractionService {
    
    /**
     * Démarre une session interactive avec l'utilisateur
     * 
     * @param documentAnalyser analyseur de documents pour le contexte
     * @param retriever retrieveur pour la recherche vectorielle
     */
    @Override
    public void startInteractiveSession(DocumentAnalyser documentAnalyser, 
                                      EmbeddingStoreContentRetriever retriever) {
        // Implémentation...
    }
}
```

#### 6. Commit et Push

```bash
# Staging des changements
git add .

# Commit avec message descriptif
git commit -m "feat: ajout de l'agent science pour les questions scientifiques

- Nouvel agent SCIENCE dans AgentType
- Interface et implémentation ScienceAgent
- Documents de base en physique et chimie
- Tests d'intégration

Closes #42"

# Push vers votre fork
git push origin feature/nom-de-votre-feature
```

#### 7. Pull Request

1. **Aller sur GitHub** et créer une Pull Request
2. **Titre descriptif** : `feat: Ajout de l'agent Science`
3. **Description détaillée** :
   - Fonctionnalités ajoutées
   - Tests effectués
   - Screenshots si interface
   - Breaking changes éventuels

### Types de Contributions

#### 🐛 Bug Reports

Utilisez le template GitHub Issues avec :
- **Description** du problème
- **Étapes** pour reproduire
- **Comportement attendu** vs observé
- **Environnement** (OS, Java, versions)
- **Logs** d'erreur

#### ✨ Nouvelles Fonctionnalités

- **Discuter** d'abord dans une Issue
- **Conception** avant implémentation
- **Tests** complets
- **Documentation** mise à jour

#### 📝 Documentation

- **Corrections** de typos
- **Améliorations** de clarté
- **Nouveaux guides** d'utilisation
- **Exemples** d'usage

#### 🔧 Améliorations Techniques

- **Optimisations** de performance
- **Refactoring** pour la maintenabilité
- **Mise à jour** des dépendances
- **Sécurité** et bonnes pratiques

### Environnement de Développement

#### IDE Recommandé

- **IntelliJ IDEA** ou **VS Code** avec extensions Java
- **Extensions utiles** :
  - Java Extension Pack
  - Maven for Java
  - Spring Boot Tools (si applicable)

#### Configuration IDE

Importez le projet comme projet Maven et configurez :

```xml
<!-- Configuration Maven dans pom.xml -->
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### Questions et Support

- **GitHub Issues** : Pour les bugs et feature requests
- **Discussions** : Pour les questions générales
- **Wiki** : Documentation détaillée (en cours)

### Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

---

**Merci de contribuer au développement du système RAG avec agents spécialisés ! 🚀**
