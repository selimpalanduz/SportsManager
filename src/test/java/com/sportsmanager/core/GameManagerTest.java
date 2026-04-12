package com.sportsmanager.core;

import com.sportsmanager.core.GameManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager gameManager;
    private FootballTeam home;
    private FootballTeam away;
    private Match match;

    @BeforeEach
    void setUp() {
        ISport sport = SportFactory.createSport("football");
        IMatchEngine engine = SportFactory.createEngine("football");
        League league = new League("Test League");

        home = new FootballTeam("Home Team");
        away = new FootballTeam("Away Team");

        for (int i = 0; i < 11; i++) {
            home.addPlayer(new FootballPlayer("Home Player " + i, 75));
            away.addPlayer(new FootballPlayer("Away Player " + i, 72));
        }

        league.addTeam(home);
        league.addTeam(away);

        match = new Match(home, away);
        gameManager = new GameManager(sport, engine, league);
    }

    @Test
    void sportShouldBeFootball() {
        assertEquals("Football", gameManager.getSport().getSportName());
    }

    @Test
    void leagueShouldHaveTwoTeams() {
        assertEquals(2, gameManager.getLeague().getTeams().size());
    }

    @Test
    void playMatchShouldReturnResult() {
        MatchResult result = gameManager.playMatch(match);
        assertNotNull(result);
    }

    @Test
    void matchShouldBePlayedAfterSimulation() {
        gameManager.playMatch(match);
        assertTrue(match.isPlayed());
    }

    @Test
    void totalHomescoreShouldNotBeNegative() {
        MatchResult result = gameManager.playMatch(match);
        assertTrue(result.getTotalHomeScore() >= 0);
    }

    @Test
    void totalAwayScoreShouldNotBeNegative() {
        MatchResult result = gameManager.playMatch(match);
        assertTrue(result.getTotalAwayScore() >= 0);
    }
}