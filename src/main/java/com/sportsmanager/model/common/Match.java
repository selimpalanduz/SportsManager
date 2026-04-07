package com.sportsmanager.model.common;

public class Match {
    private Team homeTeam;
    private Team awayTeam;
    private Lineup homeLineup;
    private Lineup awayLineup;
    private MatchResult result;
    private boolean isPlayed;

    public Match(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.isPlayed = false; // Maç başlangıçta oynanmamıştır
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Lineup getHomeLineup() {
        return homeLineup;
    }

    public void setHomeLineup(Lineup homeLineup) {
        this.homeLineup = homeLineup;
    }

    public Lineup getAwayLineup() {
        return awayLineup;
    }

    public void setAwayLineup(Lineup awayLineup) {
        this.awayLineup = awayLineup;
    }

    public MatchResult getResult() {
        return result;
    }

    public void setResult(MatchResult result) {
        this.result = result;
        this.isPlayed = true; // Sonuç girildiğinde maç oynanmış sayılır
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}