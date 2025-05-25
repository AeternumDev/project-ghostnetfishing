package com.ghostnetfishing.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;
import java.util.logging.Level;

@ApplicationScoped
public class EntityManagerProducer {

    private static final Logger logger = Logger.getLogger(EntityManagerProducer.class.getName());
    private EntityManagerFactory emf;

    @Produces
    @ApplicationScoped
    public EntityManagerFactory createEntityManagerFactory() {
        if (emf == null) {
            try {
                logger.info("Creating EntityManagerFactory for persistence unit: ghostnetPU");
                emf = Persistence.createEntityManagerFactory("ghostnetPU");
                logger.info("EntityManagerFactory created successfully");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to create EntityManagerFactory", e);
                throw new RuntimeException("Could not create EntityManagerFactory: " + e.getMessage(), e);
            }
        }
        return emf;
    }
    
    @PreDestroy
    public void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            logger.info("Closing EntityManagerFactory");
            emf.close();
        }
        
        // MySQL Connection Cleanup
        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            logger.info("MySQL Connection Cleanup completed");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to cleanup MySQL connections", e);
        }
    }
}
