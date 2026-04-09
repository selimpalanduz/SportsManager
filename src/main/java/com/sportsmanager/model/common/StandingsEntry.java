package com.sportsmanager.model.common;

public class StandingsEntry {
    private Team team;
    private int played, wins, draws, losses;
    private int goalsFor, goalsAgainst;

    public StandingsEntry(Team team) {
        this.team = team;
    }

    public Team getTeam() { return team; }
    public int getPlayed() { return played; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }

    public int getPoints() {
        return wins * 3 + draws;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void addResult(int gf, int ga) {
        played++;
        goalsFor += gf;
        goalsAgainst += ga;
        if (gf > ga) wins++;
        else if (gf == ga) draws++;
        else losses++;
        team.addPoints(gf > ga ? 3 : gf == ga ? 1 : 0);
    }
    //DenizKaraman461
}