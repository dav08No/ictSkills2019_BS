package com.example.appdev_nocito_davide;

public class Participant {

    private int id;
    private String name;
    private boolean isTemporary;
    private boolean isTeam;

    public Participant(int id, String name, boolean isTemporary, boolean isTeam) {
        this.id = id;
        this.name = name;
        this.isTemporary = isTemporary;
        this.isTeam = isTeam;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public boolean isTeam() {
        return isTeam;
    }

    public void setTeam(boolean team) {
        isTeam = team;
    }
}
