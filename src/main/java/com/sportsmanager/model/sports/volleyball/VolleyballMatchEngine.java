package com.sportsmanager.model.sports.volleyball;

import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.common.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VolleyballMatchEngine implements IMatchEngine {

    private Match match;
    private List<PeriodResult> setResults;
    private int currentSet;
    private int homeSetsWon;
    private int awaySetsWon;
    private final int SETS_TO_WIN = 3;
    private Random random;

    public VolleyballMatchEngine() {
        this.setResults = new ArrayList<>();
        this.currentSet = 0;
        this.homeSetsWon = 0;
        this.awaySetsWon = 0;
        this.random = new Random();
    }

    @Override
    public void setupMatch(Match match) {
        this.match = match;
        this.setResults = new ArrayList<>();
        this.currentSet = 0;
        this.homeSetsWon = 0;
        this.awaySetsWon = 0;
    }

    @Override
    public PeriodResult simulateNextPeriod() {
        int homeSkill = getTeamAverageSkill(match.getHomeTeam().getAvailablePlayers());
        int awaySkill = getTeamAverageSkill(match.getAwayTeam().getAvailablePlayers());

        int maxPoints = (currentSet == 4) ? 15 : 25;

        int homeScore = 0;
        int awayScore = 0;

        while (homeScore < maxPoints && awayScore < maxPoints) {
            double homeChance = (double) homeSkill / (homeSkill + awaySkill);
            if (random.nextDouble() < homeChance) {
                homeScore++;
            } else {
                awayScore++;
            }
        }

        if (homeScore > awayScore) {
            homeSetsWon++;
        } else {
            awaySetsWon++;
        }

        PeriodResult result = new PeriodResult(homeScore, awayScore);
        setResults.add(result);
        currentSet++;

        if (isMatchOver()) {
            MatchResult matchResult = new MatchResult(
                    match.getHomeTeam(),
                    match.getAwayTeam(),
                    setResults
            );
            match.setResult(matchResult);
        }

        return result;
    }

    @Override
    public boolean isMatchOver() {
        return homeSetsWon >= SETS_TO_WIN || awaySetsWon >= SETS_TO_WIN;
    }

    @Override
    public List<Player> checkInjuries() {
        List<Player> injured = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(match.getHomeTeam().getAvailablePlayers());
        allPlayers.addAll(match.getAwayTeam().getAvailablePlayers());

        for (Player p : allPlayers) {
            if (random.nextInt(100) < 3) {
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
}