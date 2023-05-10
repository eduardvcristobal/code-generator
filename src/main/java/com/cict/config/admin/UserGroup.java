package com.cict.config.admin;

public enum UserGroup {

    DEV_ADMIN("Developer and Admin"),
    DEV_QA("Development QA"),
    PARS_ADMIN("PARS Admin"),

    LINE_LEADER("Line Leader"),
    LINE_ENCODER("Line Leader"),
    MECHANIC("Mechanic"),





    DUMMY_END("Dummy Group");

    private String description;
    UserGroup(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}