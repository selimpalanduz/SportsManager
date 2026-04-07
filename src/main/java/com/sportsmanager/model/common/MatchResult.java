package com.sportsmanager.model.common;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {
    private List<PeriodResult> periods;

    public MatchResult() {
        this.periods = new ArrayList<>();
    }

    public void addPeriodResult(PeriodResult periodResult) {
        this.periods.add(periodResult);
    }

    public List<PeriodResult> getPeriods() {
        return periods;
    }

    public int getTotalHomeScore() {
        int total = 0;
        for (PeriodResult pr : periods) {
            total += pr.getHomeScore();
        }
        return total;
    }

    public int getTotalAwayScore() {
        int total = 0;
        for (PeriodResult pr : periods) {
            total += pr.getAwayScore();
        }
        return total;
    }

    // Kazanan takımı döner. Beraberlikse null döner.
    public Team getWinner(Team homeTeam, Team awayTeam) {
        int homeScore = getTotalHomeScore();
        int awayScore = getTotalAwayScore();

        if (homeScore > awayScore) {
            return homeTeam;
        } else if (awayScore > homeScore) {
            return awayTeam;
        } else {
            return null; // Beraberlik durumu
        }
    }
}