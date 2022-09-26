package com.example.Thesis_v3.models;

public class Projects {
    private int ID;
    private String name;
    private String ID_tasks;

    public Projects() {
        ID=0;
        name="error";
        ID_tasks="error";
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID_tasks() {
        return ID_tasks;
    }

    public void setID_tasks(String ID_tasks) {
        this.ID_tasks = ID_tasks;
    }

    public Projects(int ID, String name, String ID_tasks) {
        this.ID = ID;
        this.name = name;
        this.ID_tasks = ID_tasks;
    }
}
