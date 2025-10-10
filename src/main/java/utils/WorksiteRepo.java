package utils;

import enums.WorksiteType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

public final class WorksiteRepo {
    public Worksite save(Worksite w) {
        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();
            if (Long.valueOf(w.getId()) == null) {
                em.persist(w);
            } else {
                w = em.merge(w);
            }
            em.getTransaction().commit();
            return w;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error saving worksite: " + e.getMessage(), e);
        }
    }

    /** Simple SELECT by id. */
    public Optional<Worksite> findById(Long id) {
        try (EntityManager em = JPA.em()) {
            Worksite w = em.find(Worksite.class, id);
            return Optional.ofNullable(w);
        }
    }


}
