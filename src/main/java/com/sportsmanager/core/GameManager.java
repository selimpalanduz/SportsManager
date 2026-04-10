package com.sportsmanager.core;

import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.common.Player;
import java.util.List;

public class GameManager {

    private ISport sport;
    private IMatchEngine matchEngine;
    private League league;

    public GameManager(ISport sport, IMatchEngine matchEngine, League league) {
        this.sport = sport;
        this.matchEngine = matchEngine;
        this.league = league;
    }

    public MatchResult playMatch(Match match) {
        matchEngine.setupMatch(match);

        while (!matchEngine.isMatchOver()) {
            PeriodResult period = matchEngine.simulateNextPeriod();
            System.out.println("Period ended: " + period.getHomeScore() + " - " + period.getAwayScore());

            List<Player> injured = matchEngine.checkInjuries();
            for (Player p : injured) {
                System.out.println(p.getName() + " got injured!");
            }
        }

        MatchResult result = match.getResult();
        if (result.getWinner() != null) {
            System.out.println("Winner: " + result.getWinner().getName());
        } else {
            System.out.println("Draw!");
        }

        return result;
    }

    public void advanceToNextWeek() {
        System.out.println("Advanced to next week.");
    }

    public void handleEndOfSeason() {
        System.out.println("Season ended!");
    }

    public ISport getSport() { return sport; }
    public IMatchEngine getMatchEngine() { return matchEngine; }
    public League getLeague() { return league; }
    //Selim
}
