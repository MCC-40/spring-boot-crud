package com.mcc40.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrudApplication {

    public static void main(String[] args) {
//        EntityManagerFactory emf
//                = Persistence.createEntityManagerFactory("example-unit");
//        try {
//            persistEntity(emf);
//        } finally {
//            emf.close();
//        }
        SpringApplication.run(CrudApplication.class, args);
        System.out.println("Sudah berjalan, ga usah nungguin lagi!!!");
    }

//    private static void persistEntity(EntityManagerFactory emf) {
//        EntityManager em = emf.createEntityManager();
//        em.setFlushMode(FlushModeType.COMMIT);
//        List<Employee> employeeList = getNewEmployees();
//
//        em.getTransaction().begin();
//        for (Employee employee : employeeList) {
//            em.persist(employee);
//        }
//        em.flush();
//        showPersistedITEmployees(em);
//        em.getTransaction().commit();
//        em.close();
//    }

}
