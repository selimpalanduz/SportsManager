package com.sportsmanager.model.common;

import java.util.ArrayList;
import java.util.List;

public class League {
    private String name;
    private List<Team> teams;
    private Fixture fixture;
    private List<StandingsEntry> standings;

    public League(String name) {
        this.name = name;
        this.teams = new ArrayList<>();
        this.fixture = new Fixture();
        this.standings = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<Team> getTeams() { return teams; }
    public Fixture getFixture() { return fixture; }
    public List<StandingsEntry> getStandings() { return standings; }
    public void addTeam(Team team) { teams.add(team); }
    public void setStandings(List<StandingsEntry> standings) { this.standings = standings; }
    //DenizKaraman461
}