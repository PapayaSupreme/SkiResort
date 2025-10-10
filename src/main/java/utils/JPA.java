package utils;

import jakarta.persistence.*;

public final class JPA {
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("skiPU");
    private JPA() {}
    public static EntityManager em() { return EMF.createEntityManager(); }
    public static void close() { EMF.close(); }
}
