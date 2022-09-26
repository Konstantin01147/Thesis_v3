package com.example.Thesis_v3.models;

public class Commands {
    private int ID;
    private int ID_projects;
    private String ID_users;
    private int ID_kurator;
    private int ID_manager;
    private int ID_customer;
    private String nameCommand;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_projects() {
        return ID_projects;
    }

    public void setID_projects(int ID_projects) {
        this.ID_projects = ID_projects;
    }

    public String getID_users() {
        return ID_users;
    }

    public void setID_users(String ID_users) {
        this.ID_users = ID_users;
    }

    public int getID_kurator() {
        return ID_kurator;
    }

    public void setID_kurator(int ID_kurator) {
        this.ID_kurator = ID_kurator;
    }

    public int getID_manager() {
        return ID_manager;
    }



    public void setID_manager(int ID_manager) {
        this.ID_manager = ID_manager;
    }

    public int getID_customer() {
        return ID_customer;
    }

    public void setID_customer(int ID_customer) {
        this.ID_customer = ID_customer;
    }

    public String getNameCommand() {
        return nameCommand;
    }

    public void setNameCommand(String nameCommand) {
        this.nameCommand = nameCommand;
    }

    public Commands() {
    }

    public Commands(int ID, int ID_projects, String ID_users, int ID_kurator, int ID_manager, int ID_customer, String nameCommand) {
        this.ID = ID;
        this.ID_projects = ID_projects;
        this.ID_users = ID_users;
        this.ID_kurator = ID_kurator;
        this.ID_manager = ID_manager;
        this.ID_customer = ID_customer;
        this.nameCommand = nameCommand;
    }
}
