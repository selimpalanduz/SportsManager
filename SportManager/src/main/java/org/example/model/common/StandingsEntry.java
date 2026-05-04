package org.example.model.common;

public class StandingsEntry {
    Team team;
    int played, wins, draws, losses;
    int goalsFor, goalsAgainst;

    int getPoints();
    int getGoalDifference();
}
