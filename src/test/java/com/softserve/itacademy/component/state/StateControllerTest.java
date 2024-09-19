package com.softserve.itacademy.component.state;

import com.softserve.itacademy.controller.StateController;
import com.softserve.itacademy.service.StateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StateController.class)
@ExtendWith(MockitoExtension.class)
class StateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @Test
    void testListStates() throws Exception {
        when(stateService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"));

        verify(stateService, times(1)).findAll();
    }
}
