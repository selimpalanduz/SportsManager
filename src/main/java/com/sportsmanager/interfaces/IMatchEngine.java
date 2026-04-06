package com.sportsmanager.interfaces;

import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.common.Player;

import java.util.List;
public interface IMatchEngine {
    void setupMatch(Match match);
    PeriodResult simulateNextPeriod();
    boolean isMatchOver();
    List<Player> checkInjuries();
}
