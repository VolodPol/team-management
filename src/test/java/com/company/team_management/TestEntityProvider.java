package com.company.team_management;

import java.util.List;

public interface TestEntityProvider<E> {
    E generateEntity();
    List<E> generateEntityList();
}
