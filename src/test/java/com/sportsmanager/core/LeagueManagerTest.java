package com.sportsmanager.core;

import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballSport;
import com.sportsmanager.model.sports.football.FootballTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LeagueManagerTest {

    private LeagueManager leagueManager;
    private FootballTeam team1;
    private FootballTeam team2;
    private FootballTeam team3;
    private FootballSport sport;

    @BeforeEach
    void setUp() {
        leagueManager = new LeagueManager();
        team1 = new FootballTeam("Team 1");
        team2 = new FootballTeam("Team 2");
        team3 = new FootballTeam("Team 3");
        sport = new FootballSport();

        for (int i = 0; i < 11; i++) {
            team1.addPlayer(new FootballPlayer("Player " + i, 75));
            team2.addPlayer(new FootballPlayer("Player " + i, 72));
            team3.addPlayer(new FootballPlayer("Player " + i, 70));
        }
    }

    @Test
    void leagueShouldHaveCorrectTeamCount() {
        League league = leagueManager.createLeague("Test League", Arrays.asList(team1, team2, team3), sport);
        assertEquals(3, league.getTeams().size());
    }

    @Test
    void standingsShouldHaveEntryForEachTeam() {
        League league = leagueManager.createLeague("Test League", Arrays.asList(team1, team2, team3), sport);
        List<StandingsEntry> standings = league.getStandings();
        assertEquals(3, standings.size());
    }

    @Test
    void initialPointsShouldBeZero() {
        League league = leagueManager.createLeague("Test League", Arrays.asList(team1, team2), sport);
        List<StandingsEntry> standings = league.getStandings();
        for (StandingsEntry entry : standings) {
            assertEquals(0, entry.getPoints());
        }
    }

    @Test
    void winShouldGiveThreePoints() {
        StandingsEntry entry = new StandingsEntry(team1, sport);
        entry.addResult(2, 0);
        assertEquals(3, entry.getPoints());
    }

    @Test
    void drawShouldGiveOnePoint() {
        StandingsEntry entry = new StandingsEntry(team1, sport);
        entry.addResult(1, 1);
        assertEquals(1, entry.getPoints());
    }

    @Test
    void lossShouldGiveZeroPoints() {
        StandingsEntry entry = new StandingsEntry(team1, sport);
        entry.addResult(0, 2);
        assertEquals(0, entry.getPoints());
    }

    @Test
    void goalDifferenceShouldBeCorrect() {
        StandingsEntry entry = new StandingsEntry(team1, sport);
        entry.addResult(3, 1);
        assertEquals(2, entry.getGoalDifference());
    }

    @Test
    void playedCountShouldIncrease() {
        StandingsEntry entry = new StandingsEntry(team1, sport);
        entry.addResult(1, 0);
        entry.addResult(2, 1);
        assertEquals(2, entry.getPlayed());
    }
}