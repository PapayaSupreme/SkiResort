package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPA {
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("skiPU");
    private JPA() {}
    public static EntityManager em() { return EMF.createEntityManager(); }
    public static void close() { EMF.close(); }
}
