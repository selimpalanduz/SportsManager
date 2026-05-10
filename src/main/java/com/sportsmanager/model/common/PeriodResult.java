package com.sportsmanager.model.common;

import java.io.Serializable;

public class PeriodResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int homeScore;
    private int awayScore;

    public PeriodResult(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }
    //DenizKaraman461&yusuf
}