package com.company.team_management.services;

import com.company.team_management.utils.test_data_provider.ProgrammerProvider;
import com.company.team_management.utils.TestUtils;
import com.company.team_management.utils.test_data_provider.TestEntityProvider;
import com.company.team_management.entities.Programmer;
import com.company.team_management.exceptions.already_exists.EntityExistsException;
import com.company.team_management.exceptions.no_such.NoSuchEntityException;
import com.company.team_management.repositories.ProgrammerRepository;
import com.company.team_management.services.impl.ProgrammerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgrammerServiceTest {
    @Mock
    private ProgrammerRepository repository;
    @InjectMocks
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ProgrammerService service;
    private Programmer programmer1;
    private Programmer programmer2;
    private List<Programmer> programmerList;
    private final TestEntityProvider<Programmer> entityProvider = new ProgrammerProvider();

    @BeforeEach
    public void setUp() {
        programmerList = entityProvider.generateEntityList();
        programmer1 = programmerList.get(0);
        programmer2 = programmerList.get(1);
    }

    @AfterEach
    public void tearDown() {
        programmer1 = programmer2 = null;
    }

    @Test
    public void findAllEntities() {
        when(repository.findAllFetch()).thenReturn(programmerList);
        assertIterableEquals(programmerList, service.findAll());
        verify(repository, times(1)).findAllFetch();
    }

    @Test
    public void findBySpecificId() {
        final int id = TestUtils.generateId();
        programmer2.setId(id);
        when(repository.findByIdAndFetch(id)).thenReturn(Optional.ofNullable(programmer2));

        assertEquals(programmer2, service.findById(id));
        assertThrows(NoSuchEntityException.class,
                () -> service.findById(id + 1));
        verify(repository, times(2)).findByIdAndFetch(any());
    }

    @Test
    public void saveEmployeeWithNullId() {
        when(repository.save(programmer1)).thenReturn(programmer1);

        assertEquals(programmer1, service.save(programmer1));
        verify(repository, times(1)).save(programmer1);
        verify(repository, times(0)).findById(any());
    }

    @Test
    public void saveThrowsExceptionIfEmployeeAlreadyExists() {
        programmer1.setId(TestUtils.generateId());
        when(repository.findById(programmer1.getId())).thenReturn(Optional.ofNullable(programmer1));

        assertThrows(EntityExistsException.class, () -> service.save(programmer1));
        verify(repository, times(0)).save(programmer1);
        verify(repository, times(1)).findById(programmer1.getId());
    }

    @Test
    public void saveNewEmployeeWithId() {
        programmer2.setId(TestUtils.generateId());
        when(repository.findById(programmer2.getId())).thenReturn(Optional.empty());
        when(repository.save(programmer2)).thenReturn(programmer2);

        assertEquals(programmer2, service.save(programmer2));
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).save(any(Programmer.class));
    }

    @Test
    public void deleteExistingEmployeeById() {
        programmer2.setId(TestUtils.generateId());
        when(repository.findById(programmer2.getId())).thenReturn(Optional.ofNullable(programmer2));

        service.deleteById(programmer2.getId());
        verify(repository, times(1)).findById(any());
        verify(repository, times(1)).deleteById(programmer2.getId());
    }

    @Test
    public void deleteNonExistingEmployeeById() {
        final int id = TestUtils.generateId();
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> service.deleteById(id));
        verify(repository, times(0)).deleteById(any());
    }

    @Test
    public void updateNonExistingEmployee() {
        programmer1.setId(TestUtils.generateId());
        when(repository.findByIdAndFetch(programmer1.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> service.updateById(programmer1.getId(), programmer1));
        verify(repository, times(0)).save(any(Programmer.class));
    }

    @Test
    public void updateExistingEmployee() {
        final int id = TestUtils.generateId();
        programmer1.setId(id);

        when(repository.findByIdAndFetch(id)).thenReturn(Optional.ofNullable(programmer1));
        Programmer modified = copyOf(programmer1);
        modified.setEmail("updated@gmail.com");
        modified.setLevel(Programmer.Level.MIDDLE);

        assertEquals(modified, service.updateById(id, modified));
    }

    private Programmer copyOf(Programmer programmer) {
        return new Programmer.Builder()
                .addId(programmer.getId())
                .addFullName(programmer.getFullName())
                .addEmail(programmer.getEmail())
                .addType(programmer.getType())
                .addLevel(programmer.getLevel())
                .build();
    }
}