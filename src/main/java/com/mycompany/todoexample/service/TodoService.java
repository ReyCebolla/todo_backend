/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.todoexample.service;

import com.mycompany.todoexample.dao.TaskDao;
import com.mycompany.todoexample.dao.UserDao;
import com.mycompany.todoexample.exception.service.exception.TodoException;
import com.mycompany.todoexample.model.Task;
import com.mycompany.todoexample.model.User;
import jakarta.persistence.PersistenceException;
import java.util.List;

/**
 *
 * @author arnau
 */
public class TodoService {
    private UserDao userdao;
    private TaskDao taskdao;
    
    public TodoService(){
        userdao = UserDao.getInstance();
        taskdao = TaskDao.getInstance();
    }
    
    public void insertUser(User u) throws TodoException{
        if (userdao.getUserByNif(u.getNif()) != null){
            throw new TodoException("Ya existe un usuario con el nif indicado");
        }
        userdao.saveUser(u);
    }
    
    public List<User> getUsers() {
        return userdao.getAllUsers();
    }
    
     public void createTask(String userNif, Task t) throws TodoException, PersistenceException {
        User user = userdao.getUserByNif(userNif);
        if (user == null) {
            throw new TodoException("L'usuari amb NIF " + userNif + " no existeix.");
        }
        t.setUser(user);
        t.setCompleted(false);
        taskdao.save(t);
    }
     
     public List<Task> llistarTasquesPerUsuari(String nif) throws TodoException {
        User user = userdao.getUserByNif(nif);
        if (user == null) {
            throw new TodoException("L'usuari amb NIF " + nif + " no existeix.");
        }
        return taskdao.findByUserNif(nif);
    }
     
    public void marcarTascaCompletada(Integer id) throws PersistenceException {
        taskdao.marcarCompletada(id);
    }

    public void eliminarTasca(Integer id) throws PersistenceException {
        taskdao.eliminar(id);
    }
}
