package com.softserve.itacademy.component.todo;

import com.softserve.itacademy.controller.ToDoController;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoController.class)
class ToDoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoService todoService;

    @MockBean
    private UserService userService;

    @MockBean
    private TaskService taskService;

    @Test
    void testCreateToDoForm() throws Exception {
        long ownerId = 1L;
        mockMvc.perform(get("/todos/create/users/{owner_id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attribute("ownerId", ownerId));
    }

    @Test
    void testCreateToDo() throws Exception {
        long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);

        when(userService.readById(anyLong())).thenReturn(owner);
        when(todoService.create(any(ToDo.class))).thenReturn(new ToDo());

        mockMvc.perform(post("/todos/create/users/{owner_id}", ownerId)
                        .param("title", "Test ToDo")
                        .param("description", "This is a test ToDo description")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/" + ownerId));
    }

    @Test
    void testGetAllToDos() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userService.readById(anyLong())).thenReturn(user);
        when(todoService.getByUserId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/todos/all/users/{user_id}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("todos-user"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testUpdateToDo() throws Exception {
        long todoId = 1L;
        long ownerId = 1L;
        ToDo todo = new ToDo();
        todo.setId(todoId);
        todo.setCreatedAt(LocalDateTime.now());
        User owner = new User();
        owner.setId(ownerId);
        todo.setOwner(owner);

        when(todoService.readById(anyLong())).thenReturn(todo);

        mockMvc.perform(get("/todos/{todo_id}/update/users/{owner_id}", todoId, ownerId))
                .andExpect(status().isOk())
                .andExpect(view().name("update-todo"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    void testDeleteToDo() throws Exception {
        long todoId = 1L;
        long ownerId = 1L;

        mockMvc.perform(get("/todos/{todo_id}/delete/users/{owner_id}", todoId, ownerId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/" + ownerId));
    }
}
