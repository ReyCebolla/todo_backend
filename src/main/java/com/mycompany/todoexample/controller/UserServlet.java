/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.todoexample.controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.mycompany.todoexample.exception.service.exception.TodoException;
import com.mycompany.todoexample.model.User;
import com.mycompany.todoexample.service.TodoService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;


/**
 *
 * @author arnau
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/api/tasks/*"})
public class UserServlet extends HttpServlet {
    
    private TodoService todoService;
    private Gson gson;

    @Override
    public void init(ServletConfig config) throws ServletException {
        todoService = new TodoService();
        gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // Exclou el camp 'user' de Task i 'taskList' de User
                    return f.getName().equals("user") || f.getName().equals("taskList");
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            // 1. Llegir el body JSON i convertir-lo a objecte User
            User user = gson.fromJson(request.getReader(), User.class);
            // 2. Cridar el service (valida duplicats + guarda)
            todoService.insertUser(user);
            // 3. Retornar 201 Created amb l'objecte creat
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(user));
        } catch (TodoException ex) {
            // Error de negoci: usuari duplicat → 409 Conflict
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(gson.toJson(ex.getMessage()));
        } catch (PersistenceException ex) {
            // Error de BBDD inesperat → 500
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson("Error intern del servidor: " + ex.getMessage()));
        }
    }

    
    
    
    @Override
    public String getServletInfo() {
        return "API REST de Usuarios";
    }// </editor-fold>

}
