package com.sportsmanager.model.common;

import com.sportsmanager.interfaces.ISport;

import java.io.Serializable;


public class StandingsEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Team team;
    private transient ISport sport; // re-attached after load

    private int played, wins, draws, losses;
    private int goalsFor, goalsAgainst;
    private int points;

    public StandingsEntry(Team team) {
        this(team, null);
    }

    public StandingsEntry(Team team, ISport sport) {
        this.team = team;
        this.sport = sport;
    }

    public void setSport(ISport sport) {
        this.sport = sport;
    }

    public Team getTeam() { return team; }
    public int getPlayed() { return played; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getPoints() { return points; }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }


    public void addResult(int gf, int ga) {
        played++;
        goalsFor += gf;
        goalsAgainst += ga;

        int win = (sport != null) ? sport.getPointsForWin()  : 3;
        int draw = (sport != null) ? sport.getPointsForDraw() : 1;

        if (gf > ga) {
            wins++;
            points += win;
            team.addPoints(win);
        } else if (gf == ga) {
            draws++;
            points += draw;
            team.addPoints(draw);
        } else {
            losses++;

        }
    }
}