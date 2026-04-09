package com.sportsmanager.model.sports.football;

import com.sportsmanager.model.common.Player;

public class FootballPlayer extends Player {
    private int pace;
    private int shooting;
    private int passing;
    private int defending;
    private String position;

    public FootballPlayer(String name, int overallSkill) {
        super(name, overallSkill);
        this.pace = overallSkill;
        this.shooting = overallSkill;
        this.passing = overallSkill;
        this.defending = overallSkill;
        this.position = "Midfielder";
    }

    public FootballPlayer(String name, int overallSkill, String position) {
        super(name, overallSkill);
        this.pace = overallSkill;
        this.shooting = overallSkill;
        this.passing = overallSkill;
        this.defending = overallSkill;
        this.position = position;
    }

    @Override
    public String getPosition() { return position; }

    public int getPace() { return pace; }
    public int getShooting() { return shooting; }
    public int getPassing() { return passing; }
    public int getDefending() { return defending; }

    public void setPace(int pace) { this.pace = pace; }
    public void setShooting(int shooting) { this.shooting = shooting; }
    public void setPassing(int passing) { this.passing = passing; }
    public void setDefending(int defending) { this.defending = defending; }
}