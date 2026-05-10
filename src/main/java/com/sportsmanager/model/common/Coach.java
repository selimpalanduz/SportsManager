package com.sportsmanager.model.common;

import java.io.Serializable;

public abstract class Coach implements Serializable {
    private static final long serialVersionUID = 1L;

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