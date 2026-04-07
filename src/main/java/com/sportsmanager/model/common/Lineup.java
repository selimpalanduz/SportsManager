package com.sportsmanager.model.common;

import java.util.List;

public class Lineup {
    private List<Player> starters;
    private List<Player> substitutes;
    private Tactic tactic;

    public Lineup(List<Player> starters, List<Player> substitutes, Tactic tactic) {
        this.starters = starters;
        this.substitutes = substitutes;
        this.tactic = tactic;
    }

    public List<Player> getStarters() {
        return starters;
    }

    public void setStarters(List<Player> starters) {
        this.starters = starters;
    }

    public List<Player> getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(List<Player> substitutes) {
        this.substitutes = substitutes;
    }

    public Tactic getTactic() {
        return tactic;
    }

    public void setTactic(Tactic tactic) {
        this.tactic = tactic;
    }
}