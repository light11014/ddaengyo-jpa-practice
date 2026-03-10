package dev.jpa.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hello-jpa");

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}