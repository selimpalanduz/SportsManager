package com.sportsmanager.model.sports.football;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.sports.football.FootballMatchEngine;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FootballMatchEngineTest {
    private FootballMatchEngine engine;
    private Match match;
    private FootballTeam home;
    private FootballTeam away;

    @BeforeEach
    void setUp() {
        engine = new FootballMatchEngine();
        home = new FootballTeam("Home Team");
        away = new FootballTeam("Away Team");

        for (int i = 0; i < 11; i++) {
            home.addPlayer(new FootballPlayer("Home Player " + i, 75));
            away.addPlayer(new FootballPlayer("Away Player " + i, 72));
        }

        match = new Match(home, away);
        engine.setupMatch(match);
    }

    @Test
    void matchShouldNotBeOverAtStart() {
        assertFalse(engine.isMatchOver());
    }

    @Test
    void firstPeriodResultShouldNotBeNull() {
        PeriodResult result = engine.simulateNextPeriod();
        assertNotNull(result);
    }

    @Test
    void homeScoreShouldNotBeNegative() {
        PeriodResult result = engine.simulateNextPeriod();
        assertTrue(result.getHomeScore() >= 0);
    }

    @Test
    void awayScoreShouldNotBeNegative() {
        PeriodResult result = engine.simulateNextPeriod();
        assertTrue(result.getAwayScore() >= 0);
    }

    @Test
    void matchShouldNotBeOverAfterOnePeriod() {
        engine.simulateNextPeriod();
        assertFalse(engine.isMatchOver());
    }

    @Test
    void matchShouldBeOverAfterTwoPeriods() {
        engine.simulateNextPeriod();
        engine.simulateNextPeriod();
        assertTrue(engine.isMatchOver());
    }

    @Test
    void matchResultShouldBeSetAfterAllPeriods() {
        engine.simulateNextPeriod();
        engine.simulateNextPeriod();
        assertNotNull(match.getResult());
    }

    @Test
    void matchResultShouldHaveTwoPeriods() {
        engine.simulateNextPeriod();
        engine.simulateNextPeriod();
        assertEquals(2, match.getResult().getPeriods().size());
    }

    @Test
    void checkInjuriesShouldReturnList() {
        engine.simulateNextPeriod();
        assertNotNull(engine.checkInjuries());
    }


















}
