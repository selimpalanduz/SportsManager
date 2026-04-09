package com.sportsmanager.model.sports.football;

import com.sportsmanager.interfaces.ISport;

public class FootballSport implements ISport {
    @Override
    public String getSportName() { return "Football"; }

    @Override
    public int getNumberOfPeriods() { return 2; }

    @Override
    public int getPeriodDurationMinutes() { return 45; }

    @Override
    public int getPointsForWin() { return 3; }

    @Override
    public int getPointsForDraw() { return 1; }

    @Override
    public int getAllowedSubstitutions() { return 5; }

    @Override
    public int getSquadSize() { return 11; }
}
