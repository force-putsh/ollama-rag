package org.rag_sys.agent;

/**
 * Énumération des types d'agents disponibles dans le système
 */
public enum AgentType {
    GUARD("guard", "Agent de garde qui analyse et route les questions"),
    STORY("story", "Agent spécialisé dans l'analyse de récits et d'histoires"),
    MATH("math", "Agent spécialisé dans les mathématiques et calculs"),
    DROIT("droit", "Agent spécialisé dans le domaine juridique");
//    GENERAL("general", "Agent généraliste pour les questions non spécialisées");

    private final String code;
    private final String description;

    AgentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AgentType fromCode(String code) {
        for (AgentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return GUARD;
    }
}
