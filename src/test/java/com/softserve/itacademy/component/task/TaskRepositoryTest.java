package com.softserve.itacademy.component.task;

import com.softserve.itacademy.model.*;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.repository.TaskRepository;
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
public class TaskRepositoryTest {

    private final TaskRepository taskRepository;
    private final ToDoRepository toDoRepository;
    private final StateRepository stateRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepository, ToDoRepository toDoRepository, StateRepository stateRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.toDoRepository = toDoRepository;
        this.stateRepository = stateRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        toDoRepository.deleteAll();
        stateRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetByTodoId_existingTasks() {
        // Створюємо нового користувача для owner
        User owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@mail.com");
        owner.setPassword("password123");
        userRepository.save(owner); // Збереження користувача

        // Створюємо новий об'єкт ToDo з заповненим owner і createdAt
        ToDo toDo1 = new ToDo();
        toDo1.setTitle("ToDo 1");
        toDo1.setOwner(owner); // Призначаємо користувача власником
        toDo1.setCreatedAt(LocalDateTime.now()); // Встановлюємо поточну дату
        toDoRepository.save(toDo1);

        // Повторюємо для іншого ToDo
        ToDo toDo2 = new ToDo();
        toDo2.setTitle("ToDo 2");
        toDo2.setOwner(owner); // Призначаємо того ж власника
        toDo2.setCreatedAt(LocalDateTime.now()); // Встановлюємо поточну дату
        toDoRepository.save(toDo2);

        // Створюємо новий State
        State state = new State();
        state.setName("New");
        stateRepository.save(state);

        // Створюємо завдання для ToDo 1
        Task task1 = new Task();
        task1.setName("Task 1");
        task1.setPriority(TaskPriority.HIGH);
        task1.setTodo(toDo1);
        task1.setState(state);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setName("Task 2");
        task2.setPriority(TaskPriority.LOW);
        task2.setTodo(toDo1);
        task2.setState(state);
        taskRepository.save(task2);

        // Створюємо завдання для ToDo 2
        Task task3 = new Task();
        task3.setName("Task 3");
        task3.setPriority(TaskPriority.MEDIUM);
        task3.setTodo(toDo2);
        task3.setState(state);
        taskRepository.save(task3);

        // Перевіряємо метод getByTodoId для toDo1
        List<Task> tasksForToDo1 = taskRepository.getByTodoId(toDo1.getId());
        assertEquals(2, tasksForToDo1.size());

        // Перевірка полів завдань
        assertEquals("Task 1", tasksForToDo1.get(0).getName());
        assertEquals(TaskPriority.HIGH, tasksForToDo1.get(0).getPriority());
        assertEquals("Task 2", tasksForToDo1.get(1).getName());
        assertEquals(TaskPriority.LOW, tasksForToDo1.get(1).getPriority());
    }
    @Test
    void testGetByTodoId_nonExistingTodo() {
        // Перевіряємо, що для неіснуючого todoId повертається порожній список
        List<Task> tasks = taskRepository.getByTodoId(999L);
        assertThat(tasks).isEmpty();
    }

    @Test
    void testGetByTodoId_noTasksForTodo() {
        // Створюємо нового користувача для owner
        User owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@mail.com");
        owner.setPassword("password123");
        userRepository.save(owner); // Зберігаємо користувача

        // Створюємо новий об'єкт ToDo з заповненими полями owner і createdAt
        ToDo toDo = new ToDo();
        toDo.setTitle("Empty ToDo");
        toDo.setOwner(owner); // Призначаємо користувача власником
        toDo.setCreatedAt(LocalDateTime.now()); // Встановлюємо поточну дату і час
        toDoRepository.save(toDo);

        // Перевіряємо, що для цього ToDo немає завдань
        List<Task> tasks = taskRepository.getByTodoId(toDo.getId());
        assertThat(tasks).isEmpty();
    }

}
