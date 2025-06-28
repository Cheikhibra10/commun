package com.cheikh.commun.config;

public final class AuditableUtil {

    private AuditableUtil() {}

    public static String build(String action, Class<?> entityClass) {
        return switch (action.toLowerCase()) {
            case "create" -> "resource to create new <%s>".formatted(entityClass.getSimpleName());
            case "update" -> "resource to update <%s>".formatted(entityClass.getSimpleName());
            case "get_all" -> "resource to get all <%s>".formatted(entityClass.getSimpleName());
            case "get_by_id" -> "resource to get <%s> by id".formatted(entityClass.getSimpleName());
            case "delete" -> "resource to delete <%s> by id".formatted(entityClass.getSimpleName());
            default -> "resource to %s <%s>".formatted(action, entityClass.getSimpleName());
        };
    }
}

