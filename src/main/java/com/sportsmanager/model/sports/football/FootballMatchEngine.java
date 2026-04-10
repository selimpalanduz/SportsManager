package com.sportsmanager.model.sports.football;

import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.common.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class FootballMatchEngine implements IMatchEngine {
    private Match match;
    private List<PeriodResult> periodResults;
    private int currentPeriod;
    private final int TOTAL_PERIODS = 2;
    private Random random;

    public FootballMatchEngine() {
        this.periodResults = new ArrayList<>();
        this.currentPeriod = 0;
        this.random = new Random();
    }

    @Override
    public void setupMatch(Match match) {
        this.match = match;
        this.periodResults = new ArrayList<>();
        this.currentPeriod = 0;
    }

    @Override
    public PeriodResult simulateNextPeriod() {
        int homeSkill = getTeamAverageSkill(match.getHomeTeam().getAvailablePlayers());
        int awaySkill = getTeamAverageSkill(match.getAwayTeam().getAvailablePlayers());

        int homeScore = simulateGoals(homeSkill, awaySkill);
        int awayScore = simulateGoals(awaySkill, homeSkill);

        PeriodResult result = new PeriodResult(homeScore, awayScore);
        periodResults.add(result);
        currentPeriod++;

        if (isMatchOver()) {
            MatchResult matchResult = new MatchResult(
                    match.getHomeTeam(),
                    match.getAwayTeam(),
                    periodResults
            );
            match.setResult(matchResult);
        }

        return result;
    }

    @Override
    public boolean isMatchOver() {
        return currentPeriod >= TOTAL_PERIODS;
    }

    @Override
    public List<Player> checkInjuries() {
        List<Player> injured = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(match.getHomeTeam().getAvailablePlayers());
        allPlayers.addAll(match.getAwayTeam().getAvailablePlayers());

        for (Player p : allPlayers) {
            if (random.nextInt(100) < 5) {
                p.setInjuryDuration(random.nextInt(3) + 1);
                injured.add(p);
            }
        }
        return injured;
    }

    private int getTeamAverageSkill(List<Player> players) {
        if (players.isEmpty()) return 50;
        int total = 0;
        for (Player p : players) {
            total += p.getOverallSkill();
        }
        return total / players.size();
    }

    private int simulateGoals(int attackSkill, int defenseSkill) {
        double chance = (double) attackSkill / (attackSkill + defenseSkill);
        int goals = 0;
        for (int i = 0; i < 5; i++) {
            if (random.nextDouble() < chance) {
                goals++;
            }
        }
        return goals;
    }







}
