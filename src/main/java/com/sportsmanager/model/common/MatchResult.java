package com.sportsmanager.model.common;

import java.io.Serializable;
import java.util.List;

public class MatchResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Team homeTeam;
    private Team awayTeam;
    private List<PeriodResult> periods;

    public MatchResult(Team homeTeam, Team awayTeam, List<PeriodResult> periods) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.periods = periods;
    }

    public int getTotalHomeScore() {
        return periods.stream().mapToInt(PeriodResult::getHomeScore).sum();
    }

    public int getTotalAwayScore() {
        return periods.stream().mapToInt(PeriodResult::getAwayScore).sum();
    }

    public Team getWinner() {
        int home = getTotalHomeScore();
        int away = getTotalAwayScore();
        if (home > away) return homeTeam;
        if (away > home) return awayTeam;
        return null;
    }

    public List<PeriodResult> getPeriods() { return periods; }
    public Team getHomeTeam() { return homeTeam; }
    public Team getAwayTeam() { return awayTeam; }
    //DenizKaraman461&yusuf
}