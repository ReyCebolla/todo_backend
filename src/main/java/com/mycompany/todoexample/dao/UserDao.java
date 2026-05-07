/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.todoexample.dao;

import com.mycompany.todoexample.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import java.util.List;

/**
 *
 * @author arnau
 */
public class UserDao {
    private EntityManagerFactory emf;
    private static UserDao instance;
    
    public UserDao(){
        emf = Persistence.createEntityManagerFactory("my_persistence_unit");
    }
    
    public static UserDao getInstance(){
        if(instance == null){
            instance = new UserDao();
        }
        return instance;
    }
    
    public User getUserByNif(String nif){
        EntityManager em = emf.createEntityManager();
        User u = em.find(User.class, nif);
        em.close();
        return u;
    }
    
    public void saveUser(User u){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction();
            em.persist(u);
            em.getTransaction().commit();
        }catch (PersistenceException ex){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw ex;
        }finally{
            em.close();
        }
    }
    
    public List<User> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        em.close();
        return users;
    }
}
