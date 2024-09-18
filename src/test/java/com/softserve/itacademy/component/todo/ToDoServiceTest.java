package com.softserve.itacademy.component.todo;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.service.ToDoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToDoServiceTest {

    @Mock
    private ToDoRepository todoRepository;

    @InjectMocks
    private ToDoService todoService;

    private ToDo expected;

    @BeforeEach
    public void setUp() {
        expected = new ToDo();
        expected.setTitle("test todo #1");
        expected.setCreatedAt(LocalDateTime.now());
        expected.setOwner(new User());
    }

    @AfterEach
    public void tearDown() {
        expected = null;
    }

    @Test
    void testCorrectCreate() {
        when(todoRepository.save(expected)).thenReturn(expected);
        ToDo actual = todoService.create(expected);

        assertEquals(expected, actual);
        verify(todoRepository, times(1)).save(expected);
    }

    @Test
    void testExceptionCreate() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> todoService.create(null)
        );

        assertEquals("ToDo cannot be null", exception.getMessage());
        verify(todoRepository, never()).save(new ToDo());
    }

    @Test
    void testCorrectReadById() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        ToDo actual = todoService.readById(anyLong());
        assertEquals(expected, actual);
        verify(todoRepository, times(1)).findById(anyLong());
    }

    @Test
    void testExceptionReadById() {
        long id = 0L;
        Exception exception = assertThrows(EntityNotFoundException.class, ()
                -> todoService.readById(id)
        );

        assertEquals("ToDo with id " + id + " not found", exception.getMessage());
        verify(todoRepository, times(1)).findById(id);
    }

    @Test
    void testCorrectUpdate() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(todoRepository.save(expected)).thenReturn(expected);
        ToDo actual = todoService.update(expected);

        assertEquals(expected, actual);
        verify(todoRepository, times(1)).findById(anyLong());
        verify(todoRepository, times(1)).save(expected);
    }

    @Test
    void testExceptionUpdate() {
        Exception exception = assertThrows(RuntimeException.class, () -> todoService.update(null));

        assertEquals("ToDo cannot be null", exception.getMessage());
        verify(todoRepository, never()).save(any(ToDo.class));
    }

    @Test
    void testDelete() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(new ToDo()));
        doNothing().when(todoRepository).delete(any(ToDo.class));
        todoService.delete(anyLong());

        verify(todoRepository, times(1)).findById(anyLong());
        verify(todoRepository, times(1)).delete(any(ToDo.class));
    }

    @Test
    void testGetAll() {
        List<ToDo> expectedTodos = List.of(new ToDo(), new ToDo(), new ToDo());

        when(todoRepository.findAll()).thenReturn(expectedTodos);
        List<ToDo> actual = todoService.getAll();

        assertEquals(expectedTodos, actual);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void testGetByUserId() {
        List<ToDo> expectedTodos = List.of(new ToDo(), new ToDo(), new ToDo());

        when(todoRepository.getByUserId(anyLong())).thenReturn(expectedTodos);
        List<ToDo> actual = todoService.getByUserId(anyLong());
        assertEquals(expectedTodos, actual);
        verify(todoRepository, times(1)).getByUserId(anyLong());
    }
}