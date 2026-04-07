package com.sportsmanager.interfaces;

public interface ISport {
    String getSportName();
    int getNumberOfPeriods();
    int getPeriodDurationMinutes();
    int getPointsForWin();
    int getPointsForDraw();
    int getAllowedSubstitutions();
    int getSquadSize();
}
