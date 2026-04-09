package com.sportsmanager.model.common;

public abstract class Coach {
    protected String name;
    protected int experience;

    public Coach(String name, int experience) {
        this.name = name;
        this.experience = experience;
    }

    public abstract void trainPlayer(Player player);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
    //DenizKaraman461
}