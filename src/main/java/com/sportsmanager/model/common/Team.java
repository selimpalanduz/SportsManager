package com.sportsmanager.model.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected List<Player> roster;
    protected List<Coach>  coaches;
    protected int points;
    protected int xp = 0;
    protected boolean trainedThisWeek = false;

    // ── Substitute bench: 2 FWD · 2 MID · 2 DEF · 1 GK = 7 ──────
    protected List<Player> substitutes = new ArrayList<>();

    public Team(String name) {
        this.name    = name;
        this.roster  = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.points  = 0;
    }

    // ── Substitutes ───────────────────────────────────────────────

    public List<Player> getSubstitutes() { return substitutes; }

    public void setSubstitutes(List<Player> subs) { this.substitutes = subs; }

    public void clearSubstitutes() { substitutes.clear(); }

    /**
     * Auto-builds a 7-man bench from the non-starting squad members.
     * Distribution: 2 Forward · 2 Midfielder · 2 Defender · 1 Goalkeeper
     * Falls back to best-available players if positional needs can't be met.
     *
     * @param starters the 11 players already in the starting lineup
     */
    public void autoAssignSubstitutes(List<Player> starters) {
        substitutes.clear();

        // Pool: fit players NOT in the starting XI
        List<Player> pool = new ArrayList<>();
        for (Player p : roster) {
            if (!p.isInjured() && !starters.contains(p)) {
                pool.add(p);
            }
        }

        // Sort pool descending by skill
        pool.sort((a, b) -> Integer.compare(b.getOverallSkill(), a.getOverallSkill()));

        int[] needs = {2, 2, 2, 1}; // FWD, MID, DEF, GK
        String[] roles = {"Forward", "Midfielder", "Defender", "Goalkeeper"};

        List<Player> remaining = new ArrayList<>(pool);

        // First pass: fill by position
        for (int i = 0; i < roles.length; i++) {
            int filled = 0;
            List<Player> toRemove = new ArrayList<>();
            for (Player p : remaining) {
                if (filled >= needs[i]) break;
                if (p.getPosition().equalsIgnoreCase(roles[i])
                        || p.getPosition().toLowerCase().contains(roles[i].toLowerCase())) {
                    substitutes.add(p);
                    toRemove.add(p);
                    filled++;
                }
            }
            remaining.removeAll(toRemove);
        }

        // Second pass: fill any unfilled slots with best remaining
        int target = 7;
        for (Player p : remaining) {
            if (substitutes.size() >= target) break;
            substitutes.add(p);
        }
    }

    // ── Existing API ──────────────────────────────────────────────

    public List<Player> getAvailablePlayers() {
        List<Player> available = new ArrayList<>();
        for (Player p : roster) {
            if (!p.isInjured()) available.add(p);
        }
        return available;
    }

    public abstract boolean isValidLineup(Lineup lineup);

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Player> getRoster() { return roster; }
    public void setRoster(List<Player> roster) { this.roster = roster; }
    public void addPlayer(Player player) { this.roster.add(player); }

    public List<Coach> getCoaches() { return coaches; }
    public void setCoaches(List<Coach> coaches) { this.coaches = coaches; }
    public void addCoach(Coach coach) { this.coaches.add(coach); }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public void addPoints(int pointsToAdd) { this.points += pointsToAdd; }

    public int getXp() { return xp; }
    public void addXp(int amount) { this.xp += amount; }

    public boolean isTrainedThisWeek() { return trainedThisWeek; }
    public void setTrainedThisWeek(boolean trained) { this.trainedThisWeek = trained; }

    protected String managerName = "Manager";
    public String getManagerName() { return managerName; }
    public void setManagerName(String name) { this.managerName = name; }
}