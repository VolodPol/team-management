package com.company.team_management;

import com.company.team_management.entities.Department;
import com.company.team_management.entities.Programmer;
import com.company.team_management.entities.Project;
import com.company.team_management.entities.Task;
import com.company.team_management.entities.users.Role;
import com.company.team_management.entities.users.User;
import com.company.team_management.entities.users.token.Token;
import com.company.team_management.entities.users.token.TokenType;
import com.company.team_management.security.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.function.Consumer;

@Profile("development")
@Configuration
@RequiredArgsConstructor
public class DevelopmentConfiguration {
    private final PasswordEncoder encoder;
    private final JwtService tokenService;
    private final EntityManagerFactory emf;

    @Bean
    public ApplicationRunner populateDatabaseWithMockData() {
        return args ->
                executeInTransaction(em -> {
                    persistUser(em);
                    persistProjectWithTasks(em);
                    persistProgrammer(em);
                    persistDepartments(em);
                });
    }

    private void executeInTransaction(Consumer<EntityManager> consumer) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            consumer.accept(entityManager);
            transaction.commit();
        } catch (Exception exception) {
            transaction.rollback();
        } finally {
            entityManager.close();
        }
    }

    private void persistProjectWithTasks(EntityManager manager) {
        Project project = new Project.Builder()
                .addTitle("project")
                .addBudget(1000L)
                .addGoal("Some goal.")
                .build();
        project.addTask(
                Task.builder()
                        .name("task1")
                        .status(Task.Status.FINISHED)
                        .build()
        );
        project.addTask(
                Task.builder()
                        .name("task2")
                        .status(Task.Status.ACTIVE)
                        .build()
        );
        manager.persist(project);
    }

    private void persistProgrammer(EntityManager manager) {
        Programmer programmer = new Programmer.Builder()
                .addFullName("VoLodPol")
                .addEmail("volod@gmail.com")
                .addLevel(Programmer.Level.JUNIOR)
                .addType(Programmer.Type.DEVELOPER)
                .build();

        Project foundProject = (Project) manager.createQuery("select p from Project p")
                .getResultStream()
                .findAny()
                .orElseThrow();

        programmer.addProject(foundProject);
        manager.persist(programmer);
    }

    private void persistUser(EntityManager manager) {
        User admin = User.builder()
                .username("admin")
                .email("user@gmail.com")
                .role(Role.ADMIN)
                .password(encoder.encode("1234"))
                .build();
        manager.persist(admin);

        String userToken = tokenService.generateJwtToken(admin);

        Token token = Token.builder()
                .user(admin)
                .tokenType(TokenType.BEARER)
                .token(userToken)
                .build();
        manager.persist(token);
    }

    private void persistDepartments(EntityManager manager) {
        Department department1 = new Department();
        department1.setName("department 1");
        department1.setLocation("location 1");

        Department department2 = new Department();
        department2.setName("department 2");
        department2.setLocation("location 2");

        Programmer availableProgrammer = manager.find(Programmer.class, 1);
        department1.addProgrammer(availableProgrammer);
        manager.persist(department1);
        manager.persist(department2);
    }
}
