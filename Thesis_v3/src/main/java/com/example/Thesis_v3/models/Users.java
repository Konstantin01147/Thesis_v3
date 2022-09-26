package com.example.Thesis_v3.models;

public class Users {
    private int ID;
    private String name;
    private String Surname;
    private String login;
    private String password;
    private String telephone;
    private String email;

    @Override
    public String toString() {
        return "ID: " + ID + " name: " + name + " Surname: " + Surname + " login: " +
                login + " password: " + password + " telephone: " + telephone + " email: " + email + "\n";
    }

    private int isAdmin;
    private int isActived;

    public Users() {
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

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
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

    public Users(int ID, String name, String surname, String login, String password, String telephone, String email, int isAdmin, int isActived) {
        this.ID = ID;
        this.name = name;
        Surname = surname;
        this.login = login;
        this.password = password;
        this.telephone = telephone;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isActived = isActived;
    }
}
