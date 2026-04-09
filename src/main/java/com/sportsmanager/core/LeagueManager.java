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
        int week = 1;
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Match match = new Match(teams.get(i), teams.get(j));
                league.getFixture().addMatch(week, match);
                week++;
            }
        }
    }

    public List<StandingsEntry> calcStandings(League league) {
        List<StandingsEntry> standings = new ArrayList<>();
        for (Team team : league.getTeams()) {
            standings.add(new StandingsEntry(team));
        }
        return standings;
    }

    public void processMatchResult(League league, Match match) {
        if (!match.isPlayed()) return;

        MatchResult result = match.getResult();
        int homeScore = result.getTotalHomeScore();
        int awayScore = result.getTotalAwayScore();

        for (StandingsEntry entry : league.getStandings()) {
            if (entry.getTeam().equals(match.getHomeTeam()) ||
                    entry.getTeam().equals(match.getAwayTeam())) {

                if (entry.getTeam().equals(match.getHomeTeam())) {
                    entry.addResult(homeScore, awayScore);
                } else {
                    entry.addResult(awayScore, homeScore);
                }
            }
        }
    }
  //yusuf emir yılmaz
}