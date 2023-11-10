package com.company.team_management.utils.test_data_provider;

import com.company.team_management.entities.Programmer;

import java.util.List;

public class ProgrammerProvider implements TestEntityProvider<Programmer> {

    @Override
    public Programmer generateEntity() {
        return new Programmer.Builder()
                .addFullName("ivan")
                .addEmail("vanya@gmail.com")
                .addLevel(Programmer.Level.JUNIOR)
                .addType(Programmer.Type.DEVELOPER)
                .build();
    }

    @Override
    public List<Programmer> generateEntityList() {
        return List.of(
                new Programmer.Builder()
                        .addFullName("sergiy")
                        .addEmail("nemchinskiy@ukr.net")
                        .addLevel(Programmer.Level.SENIOR)
                        .addType(Programmer.Type.DEVELOPER)
                        .build(),

                new Programmer.Builder()
                        .addFullName("andrew")
                        .addEmail("garfield@gmail.com")
                        .addLevel(Programmer.Level.MIDDLE)
                        .addType(Programmer.Type.QA)
                        .build()
        );
    }
}
