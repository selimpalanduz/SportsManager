package com.sportsmanager.model.sports.football;

import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.Lineup;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.common.MatchResult;
import com.sportsmanager.model.common.PeriodResult;
import com.sportsmanager.model.common.Player;
import com.sportsmanager.model.common.Tactic;
import com.sportsmanager.model.common.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballMatchEngine implements IMatchEngine {

    private final ISport sport = SportFactory.createSport("football");

    private Match match;
    private List<PeriodResult> periodResults;
    private int currentPeriod;
    private final Random random;

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

        int n = sport.getSquadSize();
        if (match.getHomeLineup() == null) {
            match.setHomeLineup(autoLineup(match.getHomeTeam(), n));
        }
        if (match.getAwayLineup() == null) {
            match.setAwayLineup(autoLineup(match.getAwayTeam(), n));
        }
    }

    @Override
    public PeriodResult simulateNextPeriod() {
        if (isMatchOver()) return new PeriodResult(0, 0);

        int homeSkill = getLineupSkill(match.getHomeLineup(), match.getHomeTeam());
        int awaySkill = getLineupSkill(match.getAwayLineup(), match.getAwayTeam());

        homeSkill += match.getHomeTeam().getXp() / 50;
        awaySkill += match.getAwayTeam().getXp() / 50;

        Tactic homeT = getTactic(match.getHomeLineup());
        Tactic awayT = getTactic(match.getAwayLineup());

        double homeMul = tacticMultiplier(homeT);
        double awayMul = tacticMultiplier(awayT);
        if (awayT == Tactic.DEFENSIVE) homeMul *= 0.85;
        if (homeT == Tactic.DEFENSIVE) awayMul *= 0.85;

        int homeScore = (int) Math.round(simulateGoals(homeSkill, awaySkill) * homeMul);
        int awayScore = (int) Math.round(simulateGoals(awaySkill, homeSkill) * awayMul);

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
        return currentPeriod >= sport.getNumberOfPeriods();
    }

    @Override
    public List<Player> checkInjuries() {
        List<Player> injured = new ArrayList<>();

        int homeInjuryRate = match.getHomeTeam().isTrainedThisWeek() ? 10 : 5;
        int awayInjuryRate = match.getAwayTeam().isTrainedThisWeek() ? 10 : 5;

        for (Player p : startersOf(match.getHomeLineup(), match.getHomeTeam())) {
            if (!p.isInjured() && random.nextInt(100) < homeInjuryRate) {
                p.setInjuryDuration(random.nextInt(3) + 1);
                injured.add(p);
            }
        }
        for (Player p : startersOf(match.getAwayLineup(), match.getAwayTeam())) {
            if (!p.isInjured() && random.nextInt(100) < awayInjuryRate) {
                p.setInjuryDuration(random.nextInt(3) + 1);
                injured.add(p);
            }
        }
        return injured;
    }

    private Lineup autoLineup(Team team, int n) {
        List<Player> available = team.getAvailablePlayers();
        List<Player> starters = new ArrayList<>();
        List<Player> subs = new ArrayList<>();
        available.sort((a, b) -> Integer.compare(b.getOverallSkill(), a.getOverallSkill()));
        for (int i = 0; i < available.size(); i++) {
            if (i < n) starters.add(available.get(i));
            else subs.add(available.get(i));
        }
        return new Lineup(starters, subs, Tactic.BALANCED);
    }

    private int getLineupSkill(Lineup lineup, Team fallback) {
        List<Player> players = startersOf(lineup, fallback);
        if (players.isEmpty()) return 50;
        int total = 0;
        for (Player p : players) total += p.getOverallSkill();
        return total / players.size();
    }

    private List<Player> startersOf(Lineup lineup, Team fallback) {
        if (lineup != null && lineup.getStarters() != null && !lineup.getStarters().isEmpty()) {
            return lineup.getStarters();
        }
        return fallback.getAvailablePlayers();
    }

    private Tactic getTactic(Lineup lineup) {
        if (lineup == null || lineup.getTactic() == null) return Tactic.BALANCED;
        return lineup.getTactic();
    }

    private double tacticMultiplier(Tactic t) {
        if (t == null) return 1.0;
        return switch (t) {
            case OFFENSIVE -> 1.25;
            case DEFENSIVE -> 0.80;
            case BALANCED  -> 1.0;
        };
    }

    private int simulateGoals(int attackSkill, int defenseSkill) {
        double chance = (double) attackSkill / (attackSkill + defenseSkill);
        int goals = 0;
        for (int i = 0; i < 5; i++) {
            if (random.nextDouble() < chance) goals++;
        }
        return goals;
    }
}