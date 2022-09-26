package com.example.Thesis_v3.models;

import java.util.Date;

public class Tasks implements Comparable<Tasks>{
    private int ID;
    private String name;
    private Date startDate;
    private Date finishDate;
    private String IDcategories;
    private int completed;
    private Date completedDate;

    public Date getCompletedDate() {
        return completedDate;
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                ", IDcategories='" + IDcategories + '\'' +
                ", completed=" + completed +
                ", completedDate=" + completedDate +
                '}' +
                "\n";
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getIDcategories() {
        return IDcategories;
    }

    public void setIDcategories(String IDcategories) {
        this.IDcategories = IDcategories;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public Tasks(int ID, String name, Date startDate, Date finishDate, String IDcategories, int completed, Date completedDate) {
        this.ID = ID;
        this.name = name;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.IDcategories = IDcategories;
        this.completed = completed;
        this.completedDate = completedDate;
    }

    public Tasks() {
    }

    @Override
    public int compareTo(Tasks o) {
        if(getCompletedDate() == null || o.getCompletedDate()==null)
            return 0;
        return getCompletedDate().compareTo(o.getCompletedDate());
    }
}
