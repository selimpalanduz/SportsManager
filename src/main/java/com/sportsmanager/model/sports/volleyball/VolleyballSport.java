package com.sportsmanager.model.sports.volleyball;

import com.sportsmanager.interfaces.ISport;

public class VolleyballSport implements ISport {

    @Override
    public String getSportName() { return "Volleyball"; }

    @Override
    public int getNumberOfPeriods() { return 5; }

    @Override
    public int getPeriodDurationMinutes() { return 25; }

    @Override
    public int getPointsForWin() { return 3; }

    @Override
    public int getPointsForDraw() { return 0; }

    @Override
    public int getAllowedSubstitutions() { return 6; }

    @Override
    public int getSquadSize() { return 6; }
}