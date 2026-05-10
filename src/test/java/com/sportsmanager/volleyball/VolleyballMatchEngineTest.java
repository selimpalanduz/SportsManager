package com.sportsmanager.volleyball;

import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.sports.volleyball.VolleyballMatchEngine;
import com.sportsmanager.model.sports.volleyball.VolleyballPlayer;
import com.sportsmanager.model.sports.volleyball.VolleyballTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VolleyballMatchEngineTest {

    private VolleyballMatchEngine engine;
    private Match match;
    private VolleyballTeam home;
    private VolleyballTeam away;

    @BeforeEach
    void setUp() {
        engine = new VolleyballMatchEngine();
        home = new VolleyballTeam("Home Team");
        away = new VolleyballTeam("Away Team");

        for (int i = 0; i < 12; i++) {
            home.addPlayer(new VolleyballPlayer("Home Player " + i, 75));
            away.addPlayer(new VolleyballPlayer("Away Player " + i, 72));
        }

        match = new Match(home, away);
        engine.setupMatch(match);
    }

    @Test
    void matchShouldNotBeOverAtStart() {
        assertFalse(engine.isMatchOver());
    }

    @Test
    void firstSetResultShouldNotBeNull() {
        PeriodResult result = engine.simulateNextPeriod();
        assertNotNull(result);
    }

    @Test
    void firstSetWinnerShouldReachAtLeast25Points() {
        PeriodResult result = engine.simulateNextPeriod();
        int winner = Math.max(result.getHomeScore(), result.getAwayScore());
        assertTrue(winner >= 25,
                "First-set winner should reach at least 25 points, got " + winner);
    }

    @Test
    void scoresShouldNotBeNegative() {
        PeriodResult result = engine.simulateNextPeriod();
        assertTrue(result.getHomeScore() >= 0);
        assertTrue(result.getAwayScore() >= 0);
    }

    @Test
    void matchShouldNotBeOverAfterOneSet() {
        engine.simulateNextPeriod();
        assertFalse(engine.isMatchOver());
    }

    @Test
    void matchShouldBeOverAfterAtMostFiveSets() {
        for (int i = 0; i < 5 && !engine.isMatchOver(); i++) {
            engine.simulateNextPeriod();
        }
        assertTrue(engine.isMatchOver());
    }

    @Test
    void matchResultShouldBeSetAfterMatchOver() {
        while (!engine.isMatchOver()) {
            engine.simulateNextPeriod();
        }
        assertNotNull(match.getResult());
    }

    @Test
    void matchShouldHaveAtLeastThreeSets() {
        while (!engine.isMatchOver()) {
            engine.simulateNextPeriod();
        }
        assertTrue(match.getResult().getPeriods().size() >= 3,
                "Volleyball must have at least 3 sets, got "
                        + match.getResult().getPeriods().size());
    }

    @Test
    void matchShouldHaveAtMostFiveSets() {
        while (!engine.isMatchOver()) {
            engine.simulateNextPeriod();
        }
        assertTrue(match.getResult().getPeriods().size() <= 5);
    }

    @Test
    void simulateAfterMatchOverShouldNotCrash() {
        while (!engine.isMatchOver()) {
            engine.simulateNextPeriod();
        }
        assertDoesNotThrow(() -> engine.simulateNextPeriod());
    }

    @Test
    void checkInjuriesShouldReturnList() {
        engine.simulateNextPeriod();
        assertNotNull(engine.checkInjuries());
    }

    @Test
    void shouldAutoLineupWhenNoneProvided() {
        assertNotNull(match.getHomeLineup());
        assertNotNull(match.getAwayLineup());
        assertEquals(6, match.getHomeLineup().getStarters().size());
        assertEquals(6, match.getAwayLineup().getStarters().size());
    }
}