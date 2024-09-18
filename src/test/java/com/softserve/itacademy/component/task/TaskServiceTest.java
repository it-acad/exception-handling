package com.softserve.itacademy.component.task;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.repository.TaskRepository;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ToDoRepository toDoRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private TaskTransformer taskTransformer;

    @InjectMocks
    private TaskService taskService;

    private Task expected;
    private State expectedState;
    private ToDo expectedToDo;


    @BeforeEach
    public void setUp() {

        expectedState = new State();
        expectedState.setId(1L);
        expectedState.setName("New");

        expectedToDo = new ToDo();
        expectedToDo.setId(1L);
        expectedToDo.setTitle("Test ToDo");

        expected = new Task();
        expected.setId(1L);
        expected.setName("test task");
        expected.setPriority(TaskPriority.MEDIUM);
        expected.setState(expectedState);
        expected.setTodo(expectedToDo);
    }


    @AfterEach
    public void tearDown() {
        expected = null;
    }

    @Test
    public void testCorrectCreate() {

        TaskDto taskDto = new TaskDto(
                expected.getId(),
                expected.getName(),
                expected.getPriority().toString(),
                expected.getTodo().getId(),
                expected.getState().getId()
        );

        when(toDoRepository.findById(taskDto.getTodoId())).thenReturn(Optional.of(expectedToDo));
        when(stateRepository.findByName("New")).thenReturn(expectedState);
        when(taskTransformer.fillEntityFields(any(Task.class), eq(taskDto), eq(expectedToDo), eq(expectedState)))
                .thenReturn(expected);
        when(taskRepository.save(expected)).thenReturn(expected);
        when(taskTransformer.convertToDto(expected)).thenReturn(taskDto);

        TaskDto actual = taskService.create(taskDto);

        assertEquals(taskDto, actual);

        verify(toDoRepository, times(1)).findById(taskDto.getTodoId());
        verify(stateRepository, times(1)).findByName("New");
        verify(taskTransformer, times(1)).fillEntityFields(any(Task.class), eq(taskDto), eq(expectedToDo), eq(expectedState));
        verify(taskRepository, times(1)).save(expected);
        verify(taskTransformer, times(1)).convertToDto(expected);
    }


    @Test
    public void testExceptionCreate() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> taskService.create(null)
        );

        assertEquals("Task cannot be null", exception.getMessage());
        verify(taskRepository, never()).save(new Task());
    }

    @Test
    public void testCorrectReadById() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        Task actual = taskService.readById(anyLong());

        assertEquals(expected, actual);
        verify(taskRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testExceptionReadById() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> taskService.readById(0L)
        );

        assertEquals("Task with id 0 not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testCorrectUpdate() {
        TaskDto taskDto = new TaskDto(
                expected.getId(),
                expected.getName(),
                expected.getPriority().toString(),
                expected.getTodo().getId(),
                expected.getState().getId()
        );

        when(taskRepository.findById(expected.getId())).thenReturn(Optional.of(expected));
        when(toDoRepository.findById(taskDto.getTodoId())).thenReturn(Optional.of(expectedToDo));
        when(stateRepository.findById(taskDto.getStateId())).thenReturn(Optional.of(expectedState));
        when(taskTransformer.fillEntityFields(eq(expected), eq(taskDto), eq(expectedToDo), eq(expectedState)))
                .thenReturn(expected);
        when(taskRepository.save(expected)).thenReturn(expected);
        when(taskTransformer.convertToDto(expected)).thenReturn(taskDto);

        TaskDto actualDto = taskService.update(taskDto);

        assertEquals(taskDto, actualDto);

        verify(taskRepository, times(1)).findById(expected.getId());
        verify(toDoRepository, times(1)).findById(taskDto.getTodoId());
        verify(stateRepository, times(1)).findById(taskDto.getStateId());
        verify(taskTransformer, times(1)).fillEntityFields(eq(expected), eq(taskDto), eq(expectedToDo), eq(expectedState));
        verify(taskRepository, times(1)).save(expected);
        verify(taskTransformer, times(1)).convertToDto(expected);
    }


    @Test
    public void testDelete() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(new Task()));
        doNothing().when(taskRepository).delete(any(Task.class));
        taskService.delete(anyLong());

        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).delete(any(Task.class));
    }

    @Test
    public void testGetAll() {
        List<Task> expected = List.of(new Task(), new Task(), new Task());

        when(taskRepository.findAll()).thenReturn(expected);
        List<Task> actual = taskService.getAll();

        assertEquals(expected, actual);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testGetByTodoId() {
        List<Task> expected = List.of(new Task(), new Task(), new Task());

        when(taskRepository.getByTodoId(anyLong())).thenReturn(expected);
        List<Task> actual = taskService.getByTodoId(anyLong());

        assertEquals(expected, actual);
        verify(taskRepository, times(1)).getByTodoId(anyLong());
    }
}