package passes;

import enums.PassKind;
import enums.PassStatus;
import enums.PersonKind;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import people.Person;
import terrain.Lift;
import terrain.Resort;

import java.time.LocalDate;
import java.util.List;

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
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM Pass p JOIN FETCH p.owner WHERE p.owner = :owner", Pass.class)
                    .setParameter("owner", owner)
                    .getResultList();
        }
    }

    public List<PassUsage> findAllPassUsages(Person owner) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT pu FROM PassUsage pu JOIN FETCH pu.pass p WHERE p.owner = :owner", PassUsage.class)
                    .setParameter("owner", owner)
                    .getResultList();
        }
    }



    public List<Pass> findAllPasses() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p JOIN FETCH p.owner", Pass.class)
                    .getResultList();
        }
    }


    public List<Pass> findPassesOfKind(PassKind passKind) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p JOIN FETCH p.owner WHERE p.passKind = :passKind AND p.passStatus = :passStatus", Pass.class)
                    .setParameter("passKind", passKind)
                    .setParameter("passStatus", PassStatus.ACTIVE)
                    .getResultList();
        }
    }


    public List<DayPass> findDayPassesValidOn(LocalDate date){
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM DayPass p JOIN FETCH p.owner WHERE p.passStatus = :activeStatus AND p.validDay = :date", DayPass.class)
                    .setParameter("activeStatus", PassStatus.ACTIVE)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    public List<MultiDayPass> findMultiDayPassesValidOn(LocalDate date){
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM MultiDayPass p JOIN FETCH p.owner WHERE p.passStatus = :activeStatus AND :date BETWEEN p.validFrom AND p.validTo", MultiDayPass.class)
                    .setParameter("activeStatus", PassStatus.ACTIVE)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    public List<SeasonPass> findSeasonPassesValidOn(LocalDate date){
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM SeasonPass p JOIN FETCH p.owner WHERE p.passStatus = :activeStatus AND :date BETWEEN :seasonStart AND :seasonEnd", SeasonPass.class)
                    .setParameter("activeStatus", PassStatus.ACTIVE)
                    .setParameter("date", date)
                    .setParameter("seasonStart", Resort.getSeasonStart())
                    .setParameter("seasonEnd", Resort.getSeasonEnd())
                    .getResultList();
        }
    }

    public List<ALaCartePass> findALaCartePassesValidOn(LocalDate date){
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM ALaCartePass p JOIN FETCH p.owner WHERE p.passStatus = :activeStatus AND :date BETWEEN :seasonStart AND :seasonEnd", ALaCartePass.class)
                    .setParameter("activeStatus", PassStatus.ACTIVE)
                    .setParameter("date", date)
                    .setParameter("seasonStart", Resort.getSeasonStart())
                    .setParameter("seasonEnd", Resort.getSeasonEnd())
                    .getResultList();
        }
    }


    public List<Pass> findPassesOfPersonKind(PersonKind personKind) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Pass p JOIN FETCH p.owner o WHERE p.passStatus = :passStatus AND o.personKind = :personKind", Pass.class)
                    .setParameter("passStatus", PassStatus.ACTIVE)
                    .setParameter("personKind", personKind)
                    .getResultList();
        }
    }

    public void logUse(Pass pass, Lift lift) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            PassUsage usage = new PassUsage(pass, lift.getId());
            entityManager.persist(usage);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Failed to log pass use: " + e.getMessage());
        }
    }



}
