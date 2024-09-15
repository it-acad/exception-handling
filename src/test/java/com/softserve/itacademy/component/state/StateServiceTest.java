package com.softserve.itacademy.component.state;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.service.StateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateServiceTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateService stateService;

    private State expected;

    @BeforeEach
    public void setUp() {
        expected = new State();
        expected.setName("test state");
    }

    @AfterEach
    public void tearDown() {
        expected = null;
    }

    @Test
    void testCorrectCreate() {
        when(stateRepository.save(expected)).thenReturn(expected);
        State actual = stateService.create(expected);

        assertEquals(expected, actual);
        verify(stateRepository, times(1)).save(expected);
    }

    @Test
    void testExceptionCreate() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> stateService.create(null)
        );
        assertEquals("State cannot be null", exception.getMessage());
        verify(stateRepository, never()).save(new State());
    }

    @Test
    void testCorrectReadById() {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        State actual = stateService.readById(anyLong());
        assertEquals(expected, actual);
        verify(stateRepository, times(1)).findById(anyLong());
    }

    @Test
    void testExceptionReadById() {
        long id = 0L;
        Exception exception = assertThrows(RuntimeException.class, ()
                -> stateService.readById(id)
        );
        assertEquals("State with id " + id + " not found", exception.getMessage());
        verify(stateRepository, times(1)).findById(id);
    }

    @Test
    void testCorrectUpdate() {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        when(stateRepository.save(expected)).thenReturn(expected);
        State actual = stateService.update(expected);
        assertEquals(expected, actual);
        verify(stateRepository, times(1)).findById(anyLong());
        verify(stateRepository, times(1)).save(expected);
        verifyNoMoreInteractions(stateRepository);
    }

    @Test
    void testExceptionUpdate() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> stateService.update(null)
        );
        assertEquals("State cannot be null", exception.getMessage());
        verify(stateRepository, never()).save(new State());
    }

    @Test
    void testDelete() {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.of(new State()));
        doNothing().when(stateRepository).delete(any(State.class));
        stateService.delete(anyLong());
        verify(stateRepository, times(1)).findById(anyLong());
        verify(stateRepository, times(1)).delete(any(State.class));
    }

    @Test
    void testGetAll() {
        List<State> expectedStates = List.of(new State(), new State(), new State());
        when(stateRepository.findAllByOrderById()).thenReturn(expectedStates);
        List<State> actual = stateService.getAll();
        assertEquals(expectedStates, actual);
        verify(stateRepository, times(1)).findAllByOrderById();
    }

    @Test
    void testCorrectGetByName() {
        when(stateRepository.findByName(anyString())).thenReturn(expected);
        State actual = stateService.getByName(anyString());
        assertEquals(expected, actual);
        verify(stateRepository, times(1)).findByName(anyString());
    }

    @Test
    void testExceptionGetByName() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> stateService.getByName("")
        );
        assertEquals("State with name '' not found", exception.getMessage());
        verify(stateRepository, times(1)).findByName(anyString());
    }
}