package com.sportsmanager.core;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.common.Team;

import java.util.ArrayList;
import java.util.List;

public class LeagueManager {
    public League createLeague(String name, List<Team> teams) {
        League league = new League(name);
        for (Team team : teams) {
            league.addTeam(team);
        }
        generateFixture(league);
        return league;
    }

    public void generateFixture(League league) {
        List<Team> teams = league.getTeams();
        int n = teams.size();
        int week = 1;

        for (int round = 0; round < n - 1; round++) {
            for (int i = 0; i < n / 2; i++) {
                Team home = teams.get(i);
                Team away = teams.get(n - 1 - i);
                Match match = new Match(home, away);
                league.getFixture().addMatch(week, match);
            }
            // Rotating teams,round-robin algorithm
            Team last = teams.remove(teams.size() - 1);
            teams.add(1, last);
            week++;
        }
    }

    public List<StandingsEntry> calcStandings(League league) {
        if (league.getStandings() != null && !league.getStandings().isEmpty()) {
            return league.getStandings();
        }
        List<StandingsEntry> standings = new ArrayList<>();
        for (Team team : league.getTeams()) {
            standings.add(new StandingsEntry(team));
        }
        return standings;
    }

    public void processMatchResult(League league, Match match, String sportType) {
        if (!match.isPlayed()) return;

        MatchResult result = match.getResult();
        int homeGoalsFor, homeGoalsAgainst;

        if (sportType.equalsIgnoreCase("volleyball")) {
            // Set sayısını say
            int homeSets = 0, awaySets = 0;
            for (var period : result.getPeriods()) {
                if (period.getHomeScore() > period.getAwayScore()) homeSets++;
                else awaySets++;
            }
            homeGoalsFor = homeSets;
            homeGoalsAgainst = awaySets;
        } else {
            homeGoalsFor = result.getTotalHomeScore();
            homeGoalsAgainst = result.getTotalAwayScore();
        }

        for (StandingsEntry entry : league.getStandings()) {
            if (entry.getTeam().equals(match.getHomeTeam())) {
                entry.addResult(homeGoalsFor, homeGoalsAgainst);
            } else if (entry.getTeam().equals(match.getAwayTeam())) {
                entry.addResult(homeGoalsAgainst, homeGoalsFor);
            }
        }
    }
  //yusuf emir yılmaz & Selim 
}