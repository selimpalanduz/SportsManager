package com.sportsmanager.core;

import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.Coach;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.Player;
import com.sportsmanager.model.common.StandingsEntry;
import com.sportsmanager.model.common.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeagueManager implements Serializable {
    private static final long serialVersionUID = 1L;

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
                Team home = teams.get(n - 1 - i);
                Team away = teams.get(i);
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

        sortStandings(league);

        Team home = match.getHomeTeam();
        Team away = match.getAwayTeam();
        Team winner = result.getWinner();

        if (winner == null) {
            home.addXp(100);
            away.addXp(100);
        } else if (winner.equals(home)) {
            home.addXp(80);
            away.addXp(120);
        } else {
            home.addXp(120);
            away.addXp(80);
        }

        home.setTrainedThisWeek(false);
        away.setTrainedThisWeek(false);
    }

    public void sortStandings(League league) {
        List<StandingsEntry> standings = league.getStandings();
        if (standings == null) return;
        standings.sort(
                Comparator
                        .comparingInt(StandingsEntry::getPoints).reversed()
                        .thenComparing(Comparator.comparingInt(StandingsEntry::getGoalDifference).reversed())
                        .thenComparing(Comparator.comparingInt(StandingsEntry::getGoalsFor).reversed())
                        .thenComparing(e -> e.getTeam().getName())
        );
    }

    public void runWeeklyTraining(League league) {
        for (Team team : league.getTeams()) {
            int coachBonus = 0;
            for (Coach c : team.getCoaches()) coachBonus += c.getExperience();
            int gain = 50 + coachBonus * 5;
            team.addXp(gain);
            team.setTrainedThisWeek(true);
        }
    }

    public void decrementInjuries(League league) {
        for (Team team : league.getTeams()) {
            for (Player p : team.getRoster()) {
                p.decrementInjury();
            }
        }
    }

    public int totalWeeks(League league) {
        return league.getFixture().getAllMatches().keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    public void startNewSeason(League league, ISport sport) {
        league.getFixture().clear();
        List<StandingsEntry> standings = new ArrayList<>();
        for (Team t : league.getTeams()) {
            t.setPoints(0);
            for (Player p : t.getRoster()) {
                p.setInjuryDuration(0);
            }
            standings.add(new StandingsEntry(t, sport));
        }
        league.setStandings(standings);
        generateFixture(league);
    }
}