package passes;

import enums.PassStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import people.Person;

import java.util.List;
import java.util.Optional;

public class PassRepo {
    private final EntityManagerFactory entityManagerFactory;

    public PassRepo(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
    }

    public void save(Pass p) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.merge(p);   // insert or update
            entityTransaction.commit();
        } catch (RuntimeException e) {
            if (entityTransaction.isActive()){
                entityTransaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public List<Pass> findValidPasses(Person owner) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p WHERE p.owner = :owner AND p.passStatus = :activeStatus", Pass.class)
                    .setParameter("owner", owner)
                    .setParameter("activeStatus", PassStatus.ACTIVE)
                    .getResultList();
        }
    }


}
