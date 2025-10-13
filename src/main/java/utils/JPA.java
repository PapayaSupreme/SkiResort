package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public final class JPA {
    private static EntityManagerFactory entityManagerFactory;
    private JPA() {}
    public static EntityManager em() { return entityManagerFactory.createEntityManager(); }
    public static void close() { entityManagerFactory.close(); }
    public static void init(DataSource ds) {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.connection.datasource", ds);
        props.put("hibernate.transaction.jta.platform",
                "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform");
        entityManagerFactory = Persistence.createEntityManagerFactory("resortPU", props);
    }
    public static EntityManagerFactory emf() {
        if (entityManagerFactory == null) throw new IllegalStateException("JPA not initialized");
        return entityManagerFactory;
    }
}
