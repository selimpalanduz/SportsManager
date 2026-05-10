package com.sportsmanager.model.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fixture implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Integer, List<Match>> weeklyMatches;

    public Fixture() {
        this.weeklyMatches = new HashMap<>();
    }

    public void addMatch(int week, Match match) {
        weeklyMatches.computeIfAbsent(week, k -> new ArrayList<>()).add(match);
    }

    public List<Match> getMatchesByWeek(int week) {
        return weeklyMatches.getOrDefault(week, new ArrayList<>());
    }

    public Map<Integer, List<Match>> getAllMatches() {
        return weeklyMatches;
    }

    public void clear() {
        weeklyMatches.clear();
    }
    //DenizKaraman461&yusufemiryilmaz
}