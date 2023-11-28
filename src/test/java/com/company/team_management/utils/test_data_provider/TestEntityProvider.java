package com.company.team_management.utils.test_data_provider;

import java.util.List;

public interface TestEntityProvider<E> {
    E generateEntity();
    List<E> generateEntityList();
}
