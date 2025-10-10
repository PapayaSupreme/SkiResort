package utils;

import enums.WorksiteType;


public interface Worksite {
    String getName();
    long getId();

    void setName(String name);

    WorksiteType getWorksiteType();
}
