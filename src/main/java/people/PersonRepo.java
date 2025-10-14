package people;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public final class PersonRepo {
    private final EntityManagerFactory entityManagerFactory;
    public PersonRepo(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
    }
    public void save(Person p) {
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

    public List<Person> findAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Person p", Person.class)
                    .getResultList();
        }
    }

    public Optional<Person> findById(Long id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(Person.class, id));
        }
    }

    public Optional<Person> findByEmail(String email) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Person p WHERE p.email = :email", Person.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst();
        }
    }

    public List<Person> findByLastName(String lastName) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("SELECT p FROM Person p WHERE p.lastName LIKE :lastName", Person.class)
                    .setParameter("lastName", "%" + lastName + "%")
                    .getResultList();
        }
    }
}
