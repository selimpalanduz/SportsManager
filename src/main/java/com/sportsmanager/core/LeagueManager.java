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
            home.addXp(120);
            away.addXp(80);
        } else {
            home.addXp(80);
            away.addXp(120);
        }

        home.setTrainedThisWeek(false);
        away.setTrainedThisWeek(false);
    }

    public void sortStandings(League league) {
        List<StandingsEntry> standings = league.getStandings();
        if (standings == null) return;

        standings.sort((e1, e2) -> {
            if (e1.getPoints() != e2.getPoints()) {
                return Integer.compare(e2.getPoints(), e1.getPoints());
            }

            int h2h = getHeadToHead(league, e1.getTeam(), e2.getTeam());
            if (h2h != 0) return h2h;

            if (e1.getGoalDifference() != e2.getGoalDifference()) {
                return Integer.compare(e2.getGoalDifference(), e1.getGoalDifference());
            }

            if (e1.getGoalsFor() != e2.getGoalsFor()) {
                return Integer.compare(e2.getGoalsFor(), e1.getGoalsFor());
            }

            return Integer.compare(e1.getTeam().getName().compareTo(e2.getTeam().getName()), 0);
        });
    }

    private int getHeadToHead(League league, Team t1, Team t2) {
        int t1Pts = 0;
        int t2Pts = 0;

        for (List<Match> matches : league.getFixture().getAllMatches().values()) {
            for (Match m : matches) {
                if (!m.isPlayed()) continue;
                if ((m.getHomeTeam().equals(t1) && m.getAwayTeam().equals(t2)) ||
                        (m.getHomeTeam().equals(t2) && m.getAwayTeam().equals(t1))) {

                    Team winner = m.getResult().getWinner();
                    if (winner == null) {
                        t1Pts += 1;
                        t2Pts += 1;
                    } else if (winner.equals(t1)) {
                        t1Pts += 3;
                    } else if (winner.equals(t2)) {
                        t2Pts += 3;
                    }
                }
            }
        }
        return Integer.compare(t2Pts, t1Pts);
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