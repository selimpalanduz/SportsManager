package com.sportsmanager.model.common;

import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Team;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String sportType;
    private final League league;
    private final Team userTeam;
    private final int currentWeek;

    public GameState(String sportType, League league, Team userTeam, int currentWeek) {
        this.sportType = sportType;
        this.league = league;
        this.userTeam = userTeam;
        this.currentWeek = currentWeek;
    }

    public String getSportType() { return sportType; }
    public League getLeague() { return league; }
    public Team getUserTeam() { return userTeam; }
    public int getCurrentWeek() { return currentWeek; }
}