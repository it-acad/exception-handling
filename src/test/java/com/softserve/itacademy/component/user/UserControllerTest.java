package com.softserve.itacademy.component.user;

import com.softserve.itacademy.controller.UserController;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    void testCreateUserForm() throws Exception {
        mockMvc.perform(get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/create")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password123!")
                        .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void testCreateUserWithValidationErrors() throws Exception {
        mockMvc.perform(post("/users/create")
                        .param("firstName", "") // Порожній параметр для валідації
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"))
                .andExpect(model().attributeHasErrors("user"));

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void testReadUser() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setFirstName("John");
        user.setLastName("Doe");

        user.setRole(UserRole.ADMIN);

        when(userService.readById(5L)).thenReturn(user);

        mockMvc.perform(get("/users/5/read"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("firstName", is("John"))))
                .andExpect(model().attribute("user", hasProperty("lastName", is("Doe"))))
                .andExpect(model().attribute("user", hasProperty("role", is(UserRole.ADMIN))));

        verify(userService).readById(5L);
    }


    @Test
    void testUpdateUserForm() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");

        when(userService.readById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"))
                .andExpect(model().attributeExists("user", "roles"));

        verify(userService, times(1)).readById(1L);
    }

    @Test
    void testUpdateUser() throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(1L);
        updateUserDto.setFirstName("Updated Name");

        UserDto oldUserDto = new UserDto();
        oldUserDto.setId(1L);
        oldUserDto.setRole(UserRole.USER);

        when(userService.findByIdThrowing(1L)).thenReturn(oldUserDto);
        when(userService.update(any(UpdateUserDto.class))).thenReturn(new UserDto());

        mockMvc.perform(post("/users/1/update")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "Password123!")
                        .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/1/read"));

        verify(userService, times(1)).update(any(UpdateUserDto.class));
    }

    @Test
    void testUpdateUserWithValidationErrors() throws Exception {
        UserDto oldUserDto = new UserDto();
        oldUserDto.setId(1L);
        oldUserDto.setRole(UserRole.USER);

        when(userService.findByIdThrowing(1L)).thenReturn(oldUserDto);

        mockMvc.perform(post("/users/1/update")
                        .param("firstName", "")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"))
                .andExpect(model().attributeHasErrors("user"));

        verify(userService, never()).update(any(UpdateUserDto.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(get("/users/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"));

        verify(userService, times(1)).getAll();
    }
}
