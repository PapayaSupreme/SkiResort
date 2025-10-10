package people;

import jakarta.persistence.EntityManager;
import utils.JPA;
import java.util.List;

public final class PersonRepo {
    public void save(Person p) {
        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();
            if (p.getId() == null) {
                em.persist(p);
            } else {
                p = em.merge(p);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving person: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Person> findAll() {
        try (EntityManager em = JPA.em()) {
            return em.createQuery("SELECT p FROM Person p", Person.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching people: " + e.getMessage(), e);
        }
    }

    public Person findById(Long id) {
        try (EntityManager em = JPA.em()) {
            return em.find(Person.class, id);
        }
    }
}
