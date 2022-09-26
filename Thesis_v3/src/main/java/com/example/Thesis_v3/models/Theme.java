package com.example.Thesis_v3.models;

import java.util.Date;

public class Theme {
    private int ID;
    private String nameTheme;
    private String author;
    private Date creatingDate;
    private String content;

    public Theme() {
    }

    public Theme(int ID, String nameTheme, String author, Date creatingDate, String content) {
        this.ID = ID;
        this.nameTheme = nameTheme;
        this.author = author;
        this.creatingDate = creatingDate;
        this.content = content;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNameTheme() {
        return nameTheme;
    }

    public void setNameTheme(String nameTheme) {
        this.nameTheme = nameTheme;
    }

    public Date getCreatingDate() {
        return creatingDate;
    }

    public void setCreatingDate(Date creatingDate) {
        this.creatingDate = creatingDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
