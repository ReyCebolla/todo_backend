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
@WebServlet(name = "TaskServlet", urlPatterns = {"/api/tasks/*"})
public class TaskServlet extends HttpServlet {
    
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String userNif = request.getParameter("user");
        if (userNif != null && !userNif.isEmpty()) {
            try {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(todoService.llistarTasquesPerUsuari(userNif)));
            } catch (TodoException ex) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(ex.getMessage()));
            }
        }
    }

    
    @Override
    public String getServletInfo() {
        return "API REST de Usuarios";
    }// </editor-fold>
    
    /**
     * Extreu l'identificador de la URL.
     *
     * Exemples: /api/tasks → retorna null (llista totes) /api/tasks/ → retorna
     * null (llista totes) /api/tasks/5 → retorna 5 /api/tasks/abc → retorna
     * null (no és un número vàlid)
     */
    private Integer extractId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        try {
            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // =========================================================================
    //  PUT: Actualitzar
    // =========================================================================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer id = extractId(request);

        if (id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        } else {
            // PUT /api/tasks/{id} → Actualitza la tasca
            try {
                todoService.marcarTascaCompletada(id);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (PersistenceException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson("Error intern del servidor."));
            }
        }
    }

    // =========================================================================
    //  DELETE: Eliminar
    // =========================================================================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer id = extractId(request);

        if (id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            // DELETE /api/tasks/{id} → Elimina la tasca
            try {
                todoService.eliminarTasca(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (PersistenceException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson("Error intern del servidor."));
            }
        }
    }

}
