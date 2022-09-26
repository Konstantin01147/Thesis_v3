package com.example.Thesis_v3.models;

public class Manager {
    private int ID;
    private String Name;

    public Manager() {
    }

    private String Surname;
    private String login;
    private String password;
    private int isAdmin;
    private int isActived;
    private String telephone;
    private String email;
    private String idCommand;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getIsActived() {
        return isActived;
    }

    public void setIsActived(int isActived) {
        this.isActived = isActived;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCommand() {
        return idCommand;
    }

    public void setIdCommand(String idCommand) {
        this.idCommand = idCommand;
    }

    public Manager(int ID, String name, String surname, String login, String password, int isAdmin, int isActived, String telephone, String email, String idCommand) {
        this.ID = ID;
        Name = name;
        Surname = surname;
        this.login = login;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isActived = isActived;
        this.telephone = telephone;
        this.email = email;
        this.idCommand = idCommand;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }
}
