/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.todoexample.dao;

import com.mycompany.todoexample.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import java.util.List;


/**
 *
 * @author arnau
 */
public class TaskDao {
    private EntityManagerFactory emf;
    private static TaskDao instance;
    
    public TaskDao(){
        emf = Persistence.createEntityManagerFactory("my_persistence_unit");
    }
    
    public static TaskDao getInstance(){
        if(instance == null){
            instance = new TaskDao();
        }
        return instance;
    }
    
        public void save(Task t) throws PersistenceException {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(t);
                em.getTransaction().commit();
            } catch (PersistenceException ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw ex;
            } finally {
                em.close();
            }
        }
        
        public List<Task> findByUserNif(String nif) {
            EntityManager em = emf.createEntityManager();
            try {
                return em.createQuery(
                    "SELECT t FROM Task t WHERE t.user.nif = :nif", Task.class)
                    .setParameter("nif", nif)
                    .getResultList();
            } finally {
                em.close();
            }
        } 
        
        public void marcarCompletada(Integer id) throws PersistenceException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Task task = em.find(Task.class, id);
            if (task != null) {
                task.setCompleted(true);
            }
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void eliminar(Integer id) throws PersistenceException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Task task = em.find(Task.class, id);
            if (task != null) {
                em.remove(task);
            }
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
        
}
