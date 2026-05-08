package com.sportsmanager.core;

import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.common.Team;

import java.util.ArrayList;
import java.util.List;


public class LeagueManager {

    public League createLeague(String name, List<Team> teams, ISport sport) {
        League league = new League(name);
        for (Team team : teams) {
            league.addTeam(team);
        }
        generateFixture(league);

        List<StandingsEntry> standings = new ArrayList<>();
        for (Team t : league.getTeams()) {
            standings.add(new StandingsEntry(t, sport));
        }
        league.setStandings(standings);
        return league;
    }


    public League createLeague(String name, List<Team> teams) {
        return createLeague(name, teams, null);
    }


    public void generateFixture(League league) {
        List<Team> teams = new ArrayList<>(league.getTeams());
        int n = teams.size();
        if (n < 2) return;


        boolean hasBye = (n % 2 != 0);
        if (hasBye) {
            teams.add(null);
            n++;
        }

        int week = 1;
        int rounds = n - 1;


        for (int round = 0; round < rounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                Team home = teams.get(i);
                Team away = teams.get(n - 1 - i);
                if (home != null && away != null) {
                    league.getFixture().addMatch(week, new Match(home, away));
                }
            }

            Team last = teams.remove(teams.size() - 1);
            teams.add(1, last);
            week++;
        }


        for (int round = 0; round < rounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                Team home = teams.get(n - 1 - i); // swapped
                Team away = teams.get(i);          // swapped
                if (home != null && away != null) {
                    league.getFixture().addMatch(week, new Match(home, away));
                }
            }
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
        int homeFor, homeAgainst;

        if ("volleyball".equalsIgnoreCase(sportType)) {

            int homeSets = 0, awaySets = 0;
            for (var period : result.getPeriods()) {
                if (period.getHomeScore() > period.getAwayScore()) homeSets++;
                else awaySets++;
            }
            homeFor = homeSets;
            homeAgainst = awaySets;
        } else {
            homeFor = result.getTotalHomeScore();
            homeAgainst = result.getTotalAwayScore();
        }

        for (StandingsEntry entry : league.getStandings()) {
            if (entry.getTeam().equals(match.getHomeTeam())) {
                entry.addResult(homeFor, homeAgainst);
            } else if (entry.getTeam().equals(match.getAwayTeam())) {
                entry.addResult(homeAgainst, homeFor);
            }
        }
    }


    public int totalWeeks(League league) {
        return league.getFixture().getAllMatches().keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
    //yusuf emir yılmaz& selim
}