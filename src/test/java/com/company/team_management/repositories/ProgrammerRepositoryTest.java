package com.company.team_management.repositories;

import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.entities.Programmer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProgrammerRepositoryTest {

    @Autowired
    private ProgrammerRepository repository;
    private Programmer programmer;
    private final TestEntityProvider<Programmer> entityProvider = new ProgrammerProvider();

    @BeforeEach
    public void setUp() {
        repository.deleteAllInBatch();
        programmer = entityProvider.generateEntity();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        programmer = null;
    }

    @Test
    public void saveProductCase() {
        Programmer found = repository
                .findById(extractIdOfSavedEmployee())
                .orElse(null);

        assertEquals(programmer, found);
    }

    @Test
    public void getAllCase() {
        List<Programmer> programmerList = entityProvider.generateEntityList();
        repository.saveAll(programmerList);

        List<Programmer> fetched = repository.findAll();
        assertAll(
                () -> assertEquals(programmerList.get(0).getEmail(), fetched.get(0).getEmail()),
                () -> assertEquals(programmerList.get(1).getFullName(), fetched.get(1).getFullName())
        );
    }

    @Test
    public void getByIdCase() {
        int id = extractIdOfSavedEmployee();
        Programmer actual = repository.findById(id)
                .orElse(null);

        assertEquals(programmer, actual);
    }

    @Test
    public void updateExistingEmployeeCase() {
        int id = extractIdOfSavedEmployee();
        String newName = "jeremy";
        Programmer.Level newLevel = Programmer.Level.MIDDLE;

        programmer.setFullName(newName);
        programmer.setLevel(newLevel);
        repository.save(programmer);

        Programmer changed = repository.findById(id).orElse(new Programmer());
        assertEquals(newName, changed.getFullName());
        assertEquals(newLevel, changed.getLevel());
    }

    @Test
    public void deleteEmployeeCase() {
        repository.deleteById(extractIdOfSavedEmployee());

        assertEquals(0, repository.findAll().size());
    }

    private int extractIdOfSavedEmployee() {
        repository.save(programmer);
        return programmer.getId();
    }
}