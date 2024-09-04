package com.softserve.itacademy.component.task;


import com.softserve.itacademy.ToDoListApplication;
import com.softserve.itacademy.controller.TaskController;
import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {TaskController.class})
@ContextConfiguration(classes = {TaskController.class, TaskTransformer.class})
class TaskControllerTest {

    @MockBean
    private TaskService taskService;
    @MockBean
    private ToDoService todoService;
    @MockBean
    private StateService stateService;
    private final TaskTransformer taskTransformer = new TaskTransformer();

    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateGetMethod() throws Exception {

        ToDo todo = new ToDo();
        todo.setId(1);

        when(todoService.readById(1L)).thenReturn(todo);

        mvc.perform(get("/tasks/create/todos/1")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("create-task"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("task", new TaskDto()))
                .andExpect(model().attribute("todo", todo))
                .andExpect(model().attribute("priorities", TaskPriority.values()))
                .andDo(print());

        verify(todoService, times(1)).readById(1);

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void testCorrectCreatePostMethod() throws Exception {

        ToDo todo = new ToDo();
        todo.setId(1);

        when(todoService.readById(anyLong())).thenReturn(todo);
        when(stateService.getByName(anyString())).thenReturn(new State());
        when(taskService.create(any(TaskDto.class))).thenReturn(new TaskDto());

        mvc.perform(post("/tasks/create/todos/1")
                        .param("name", "Task #1")
                        .param("priority", TaskPriority.LOW.name())
                        .param("todoId", "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"))
                .andDo(print());

        verify(taskService, times(1)).create(any(TaskDto.class));

        verifyNoMoreInteractions(todoService, stateService, taskService);
    }

    @Test
    void testErrorCreatePostMethod() throws Exception {

        ToDo todo = new ToDo();
        todo.setId(1);

        TaskDto taskDto = new TaskDto(0, "", TaskPriority.LOW.name(), todo.getId(), 0);

        when(todoService.readById(anyLong())).thenReturn(todo);

        mvc.perform(post("/tasks/create/todos/1")
                        .param("name", taskDto.getName())
                        .param("priority", taskDto.getPriority())
                        .param("todoId", String.valueOf(taskDto.getTodoId()))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk())
                .andExpect(view().name("create-task"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("task", taskDto))
                .andExpect(model().attribute("todo", todo))
                .andExpect(model().attribute("priorities", TaskPriority.values()))
                .andDo(print());

        verify(todoService, times(1)).readById(anyLong());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void testUpdateGetMethod() throws Exception {

        ToDo todo = new ToDo();
        todo.setId(1);

        State state = new State();
        state.setId(1);

        Task task = new Task();
        task.setId(1);
        task.setName("Task #1");
        task.setPriority(TaskPriority.LOW);
        task.setTodo(todo);
        task.setState(state);

        TaskDto taskDto = taskTransformer.convertToDto(task);

        when(taskService.readById(anyLong())).thenReturn(task);
        when(stateService.getAll()).thenReturn(Collections.singletonList(new State()));

        mvc.perform(get("/tasks/1/update/todos/1")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("update-task"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("task", taskDto))
                .andExpect(model().attribute("priorities", TaskPriority.values()))
                .andExpect(model().attribute("states", Collections.singletonList(new State())))
                .andDo(print());

        verify(taskService, times(1)).readById(anyLong());
        verify(stateService, times(1)).getAll();

        verifyNoMoreInteractions(taskService, stateService);
    }

    @Test
    void testCorrectUpdatePostMethod() throws Exception {

        when(todoService.readById(anyLong())).thenReturn(new ToDo());
        when(stateService.readById(anyLong())).thenReturn(new State());
        when(taskService.update(any(Task.class))).thenReturn(new Task());

        mvc.perform(post("/tasks/1/update/todos/1")
                        .param("id", "1")
                        .param("name", "Task #1")
                        .param("priority", TaskPriority.LOW.name())
                        .param("stateId", "1")
                        .param("todoId", "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"))
                .andDo(print());

        verify(todoService, times(1)).readById(anyLong());
        verify(stateService, times(1)).readById(anyLong());
        verify(taskService, times(1)).update(any(Task.class));

        verifyNoMoreInteractions(todoService, stateService, taskService);
    }

    @Test
    void testErrorUpdatePostMethod() throws Exception {

        TaskDto taskDto = new TaskDto(1, "", TaskPriority.LOW.name(), 1, 1);

        when(stateService.getAll()).thenReturn(Collections.singletonList(new State()));

        mvc.perform(post("/tasks/1/update/todos/1")
                        .param("id", String.valueOf(taskDto.getId()))
                        .param("name", taskDto.getName())
                        .param("priority", taskDto.getPriority())
                        .param("stateId", String.valueOf(taskDto.getStateId()))
                        .param("todoId", String.valueOf(taskDto.getTodoId()))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk())
                .andExpect(view().name("update-task"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("task", taskDto))
                .andExpect(model().attribute("priorities", TaskPriority.values()))
                .andExpect(model().attribute("states", Collections.singletonList(new State())))
                .andDo(print());

        verify(stateService, times(1)).getAll();

        verifyNoMoreInteractions(stateService);
    }

    @Test
    void testDeleteGetMethod() throws Exception {

        doNothing().when(taskService).delete(anyLong());

        mvc.perform(get("/tasks/1/delete/todos/1")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"))
                .andDo(print());

        verify(taskService, times(1)).delete(anyLong());

        verifyNoMoreInteractions(taskService);
    }

}
