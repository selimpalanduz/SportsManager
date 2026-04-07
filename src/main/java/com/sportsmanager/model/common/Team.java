package com.sportsmanager.model.common;

import java.util.ArrayList;
import java.util.List;

public abstract class Team {
    private String name;
    private List<Player> roster;
    private List<Coach> coaches;
    private int points; // Ligdeki genel puanı

    public Team(String name) {
        this.name = name;
        this.roster = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.points = 0;
    }

    // Kadro geçerliliği (örn. 7 as, 3 yedek) seçilen spora göre değişecek
    public abstract boolean isValidLineup(Lineup lineup);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getRoster() {
        return roster;
    }

    public void setRoster(List<Player> roster) {
        this.roster = roster;
    }

    public void addPlayer(Player player) {
        this.roster.add(player);
    }

    public List<Coach> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<Coach> coaches) {
        this.coaches = coaches;
    }

    public void addCoach(Coach coach) {
        this.coaches.add(coach);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }
}