package com.ghostnetfishing.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Alternative EntityManagerProducer mit H2-Datenbank
 * Aktivieren durch Hinzufügen von @Alternative in beans.xml
 */
@ApplicationScoped
@Alternative
public class H2EntityManagerProducer {

    private static final Logger logger = Logger.getLogger(H2EntityManagerProducer.class.getName());
    private EntityManagerFactory emf;

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createH2EntityManagerFactory() {
        if (emf == null) {
            try {
                logger.info("Creating H2 EntityManagerFactory for persistence unit: ghostnetPU-h2");
                emf = Persistence.createEntityManagerFactory("ghostnetPU-h2");
                logger.info("H2 EntityManagerFactory created successfully");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to create H2 EntityManagerFactory", e);
                throw new RuntimeException("Could not create H2 EntityManagerFactory: " + e.getMessage(), e);
            }
        }
        return emf;
    }
    
    @PreDestroy
    public void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            logger.info("Closing H2 EntityManagerFactory");
            emf.close();
        }
    }
}
