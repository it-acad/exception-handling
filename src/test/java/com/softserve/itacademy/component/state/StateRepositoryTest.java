package com.softserve.itacademy.component.state;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.repository.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never"
})
public class StateRepositoryTest {

    private final StateRepository stateRepository;

    @Autowired
    public StateRepositoryTest(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @BeforeEach
    void setup() {
        stateRepository.deleteAll();
    }

    @Test
    void testFindByName_existingState() {

        State state1 = new State();
        state1.setName("Active");
        stateRepository.save(state1);

        State state2 = new State();
        state2.setName("Inactive");
        stateRepository.save(state2);

        State expected = stateRepository.findByName("Inactive");
        assertThat(expected).isNotNull();
        assertEquals("Inactive", expected.getName());
    }

    @Test
    void testFindByName_nonExistingState() {

        Optional<State> actual = Optional.ofNullable(stateRepository.findByName("NonExistent"));
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindAllByOrderById() {

        State state1 = new State();
        state1.setName("Active");
        stateRepository.save(state1);

        State state2 = new State();
        state2.setName("Inactive");
        stateRepository.save(state2);

        List<State> states = stateRepository.findAllByOrderById();
        assertThat(states).hasSize(2);


        assertThat(states.get(0)).usingRecursiveComparison().isEqualTo(state1);
        assertThat(states.get(1)).usingRecursiveComparison().isEqualTo(state2);
    }

    @Test
    void testFindAllByOrderById_emptyDatabase() {
        List<State> states = stateRepository.findAllByOrderById();
        assertThat(states).isEmpty();
    }
}
