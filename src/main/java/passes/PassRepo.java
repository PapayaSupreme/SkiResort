package passes;

import enums.PassKind;
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
            return entityManager.createQuery("SELECT p FROM Pass p WHERE p.owner = :owner AND p.passStatus = :passStatus", Pass.class)
                    .setParameter("owner", owner)
                    .setParameter("passStatus", PassStatus.ACTIVE)
                    .getResultList();
        }
    }

    public List<Pass> findAllPasses(Person owner) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p WHERE p.owner = :owner", Pass.class)
                    .setParameter("owner", owner)
                    .getResultList();
        }
    }


    public List<Pass> findAllPasses() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p", Pass.class)
                    .getResultList();
        }
    }


    public List<Pass> findPassesOfKind(PassKind passKind) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p WHERE p.passKind = :passKind AND p.passStatus = :passStatus", Pass.class)
                    .setParameter("passKind", passKind)
                    .setParameter("passStatus", PassStatus.ACTIVE)
                    .getResultList();
        }
    }


}
