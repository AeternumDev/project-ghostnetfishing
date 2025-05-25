package com.ghostnetfishing.service;

import com.ghostnetfishing.model.GhostNet;
import com.ghostnetfishing.model.NetStatus;
import com.ghostnetfishing.model.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;

@ApplicationScoped
public class GhostNetService {

    @Inject
    private EntityManagerFactory emf;
    
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }    public void reportNet(GhostNet net, Person reportingPerson) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            if (reportingPerson != null && reportingPerson.getName() != null && !reportingPerson.getName().isEmpty()) {
                if (reportingPerson.getId() == null) { // New person
                    em.persist(reportingPerson);
                } else { // Existing person
                    reportingPerson = em.merge(reportingPerson);
                }
                net.setReportingPerson(reportingPerson);
            }
            // Status is now set by the user in the form, so we don't override it here
            // Only set default status if none is provided
            if (net.getStatus() == null) {
                net.setStatus(NetStatus.REPORTED);
            }
            em.persist(net);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error reporting net", e);
        } finally {
            em.close();
        }
    }

    public void assignRecovery(Long netId, Person recoveringPerson) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            GhostNet net = em.find(GhostNet.class, netId);
            if (net != null && net.getStatus() == NetStatus.REPORTED) {
                if (recoveringPerson.getId() == null) { // New person, should ideally be registered first
                    em.persist(recoveringPerson);
                } else {
                    recoveringPerson = em.merge(recoveringPerson);
                }
                net.setRecoveringPerson(recoveringPerson);
                net.setStatus(NetStatus.RECOVERY_PENDING);
                em.merge(net);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error assigning recovery", e);
        } finally {
            em.close();
        }
    }

    public List<GhostNet> getNetsToRecover() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT gn FROM GhostNet gn WHERE gn.status = :status ORDER BY gn.id", GhostNet.class)
                     .setParameter("status", NetStatus.RECOVERY_PENDING)
                     .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<GhostNet> getAllNets() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT gn FROM GhostNet gn ORDER BY gn.id", GhostNet.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public void markAsRecovered(Long netId, Person recoveringPerson) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            GhostNet net = em.find(GhostNet.class, netId);
            // Ensure the person marking as recovered is the one assigned or has rights
            if (net != null && net.getStatus() == NetStatus.RECOVERY_PENDING && net.getRecoveringPerson() != null && net.getRecoveringPerson().getId().equals(recoveringPerson.getId())) {
                net.setStatus(NetStatus.RECOVERED);
                em.merge(net);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error marking as recovered", e);
        } finally {
            em.close();
        }
    }

    public void markAsLost(Long netId, Person reportingPerson) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            GhostNet net = em.find(GhostNet.class, netId);
            if (net != null && (net.getStatus() == NetStatus.REPORTED || net.getStatus() == NetStatus.RECOVERY_PENDING)) {
                if (reportingPerson.getId() == null) { // New person, should ideally be registered first
                    em.persist(reportingPerson);
                } else {
                    reportingPerson = em.merge(reportingPerson);
                }
                // Optionally, log who marked it as lost, though not explicitly in GhostNet entity yet
                net.setStatus(NetStatus.LOST);
                em.merge(net);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error marking as lost", e);
        } finally {
            em.close();
        }
    }

    public GhostNet findNetById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GhostNet.class, id);
        } finally {
            em.close();
        }
    }

    public Person findPersonById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public List<Person> getAllRecoverers() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Person p WHERE p.type = :type ORDER BY p.name", Person.class)
                     .setParameter("type", com.ghostnetfishing.model.PersonType.RECOVERER)
                     .getResultList();
        } finally {
            em.close();
        }
    }
    
    public Person createPerson(Person person) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(person);
            transaction.commit();
            return person;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error creating person", e);
        } finally {
            em.close();
        }
    }
}
