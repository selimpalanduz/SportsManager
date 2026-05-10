package com.sportsmanager.util;

import com.sportsmanager.model.common.League;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final League league;
    private final String sportType;
    private final String userTeamName;
    private final int currentWeek;

    public GameState(League league, String sportType, String userTeamName, int currentWeek) {
        this.league = league;
        this.sportType = sportType;
        this.userTeamName = userTeamName;
        this.currentWeek = currentWeek;
    }

    public League getLeague() { return league; }
    public String getSportType() { return sportType; }
    public String getUserTeamName() { return userTeamName; }
    public int getCurrentWeek() { return currentWeek; }
}