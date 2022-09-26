package com.example.Thesis_v3.models;

public class Customers {
    private int ID;
    private String Name;



    private String Surname;
    private String login;
    private String password;

    private int isAdmin;
    private int isActived;
    private String email;
    private String idCommand;
    private String telephone;

    public Customers() {
    }

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

    public Customers(int ID, String name, String surname, String login, String password, int isAdmin, int isActived, String email, String idCommand, String telephone) {
        this.ID = ID;
        Name = name;
        Surname = surname;
        this.login = login;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isActived = isActived;
        this.email = email;
        this.idCommand = idCommand;
        this.telephone = telephone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }
}
