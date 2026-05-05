package com.sportsmanager.model.sports.volleyball;

import com.sportsmanager.model.common.Player;

public class VolleyballPlayer extends Player {

    private int serving;
    private int blocking;
    private int digging;
    private String position;

    public VolleyballPlayer(String name, int overallSkill, String position) {
        super(name, overallSkill);
        this.serving = overallSkill;
        this.blocking = overallSkill;
        this.digging = overallSkill;
        this.position = position;


    }

    public VolleyballPlayer(String name, int overallSkill) {

        this(name,  overallSkill, "Outside Hitter");
    }

    @Override
    public String getPosition() {
        return position;

    }

    public int getServing() {
        return serving;
    }
    public int getBlocking() {
        return blocking;
    }
    public int getDigging() {
        return digging;
    }
    public void setServing(int serving) {
        this.serving = serving;
    }
    public void setBlocking(int blocking) {
        this.blocking = blocking;
    }
    public void setDigging(int digging) {
        this.digging = digging;
    }
}