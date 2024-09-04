package com.softserve.itacademy.component.user;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User expected;

    @BeforeEach
    public void setUp() {
        expected = new User();
        expected.setFirstName("Mike");
        expected.setLastName("Green");
        expected.setEmail("green@mail.com");
        expected.setPassword("1111");
    }

    @AfterEach
    public void tearDown() {
        expected = null;
    }

    @Test
    void testCorrectCreate() {
        when(userRepository.save(expected)).thenReturn(expected);
        User actual = userService.create(expected);

        assertEquals(expected, actual);
        verify(userRepository, times(1)).save(expected);
    }

    @Test
    void testExceptionCreate() {
        Exception exception = assertThrows(RuntimeException.class, ()
                -> userService.create(null)
        );

        assertEquals("User cannot be null", exception.getMessage());
        verify(userRepository, never()).save(new User());
    }

    @Test
    void testCorrectReadById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expected));
        User actual = userService.readById(anyLong());

        assertEquals(expected, actual);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void testExceptionReadById() {
        long id = 0l;
        Exception exception = assertThrows(EntityNotFoundException.class, ()
                -> userService.readById(id)
        );

        assertEquals("User with id "+ id + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }


    @Test
    void testDelete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        doNothing().when(userRepository).delete(any(User.class));
        userService.delete(anyLong());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testGetAll() {
        List<User> expectedUsers = List.of(new User(), new User(), new User());

        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> actual = userService.getAll();

        assertEquals(expectedUsers, actual);
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void testExceptionLoadUserByUsername() {
        assertThat(userService.findByUsername(anyString())).isEmpty();
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}
