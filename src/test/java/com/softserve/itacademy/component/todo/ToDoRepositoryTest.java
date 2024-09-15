package com.softserve.itacademy.component.todo;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

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
public class ToDoRepositoryTest {

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        toDoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetByUserId_asOwner() {

        User owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@mail.com");
        owner.setPassword("password123");
        userRepository.save(owner);

        ToDo toDo1 = new ToDo();
        toDo1.setTitle("ToDo 1");
        toDo1.setOwner(owner);
        toDo1.setCreatedAt(LocalDateTime.now());
        toDoRepository.save(toDo1);

        ToDo toDo2 = new ToDo();
        toDo2.setTitle("ToDo 2");
        toDo2.setOwner(owner);
        toDo2.setCreatedAt(LocalDateTime.now());
        toDoRepository.save(toDo2);

        List<ToDo> todos = toDoRepository.getByUserId(owner.getId());

        assertEquals(2, todos.size());
        assertEquals("ToDo 1", todos.get(0).getTitle());
        assertEquals("ToDo 2", todos.get(1).getTitle());
    }

    @Test
    void testGetByUserId_asCollaborator() {

        User collaborator = new User();
        collaborator.setFirstName("Jane");
        collaborator.setLastName("Smith");
        collaborator.setEmail("jane.smith@mail.com");
        collaborator.setPassword("password456");
        userRepository.save(collaborator);

        User owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@mail.com");
        owner.setPassword("password123");
        userRepository.save(owner);

        ToDo toDo = new ToDo();
        toDo.setTitle("Collaborative ToDo");
        toDo.setOwner(owner);
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setCollaborators(List.of(collaborator));
        toDoRepository.save(toDo);

        List<ToDo> todos = toDoRepository.getByUserId(collaborator.getId());
        assertEquals(1, todos.size());
        assertEquals("Collaborative ToDo", todos.get(0).getTitle());
    }

    @Test
    void testGetByUserId_asOwnerAndCollaborator() {

        User user = new User();
        user.setFirstName("Chris");
        user.setLastName("Evans");
        user.setEmail("chris.evans@mail.com");
        user.setPassword("password789");
        userRepository.save(user);

        ToDo toDoAsOwner = new ToDo();
        toDoAsOwner.setTitle("Owner ToDo");
        toDoAsOwner.setOwner(user);
        toDoAsOwner.setCreatedAt(LocalDateTime.now());
        toDoRepository.save(toDoAsOwner);

        User anotherOwner = new User();
        anotherOwner.setFirstName("Mark");
        anotherOwner.setLastName("Johnson");
        anotherOwner.setEmail("mark.johnson@mail.com");
        anotherOwner.setPassword("password321");
        userRepository.save(anotherOwner);

        ToDo toDoAsCollaborator = new ToDo();
        toDoAsCollaborator.setTitle("Collaborator ToDo");
        toDoAsCollaborator.setOwner(anotherOwner);
        toDoAsCollaborator.setCreatedAt(LocalDateTime.now());
        toDoAsCollaborator.setCollaborators(List.of(user));
        toDoRepository.save(toDoAsCollaborator);

        List<ToDo> todos = toDoRepository.getByUserId(user.getId());
        assertEquals(2, todos.size());
        assertThat(todos).extracting(ToDo::getTitle).containsExactlyInAnyOrder("Owner ToDo", "Collaborator ToDo");
    }
}
