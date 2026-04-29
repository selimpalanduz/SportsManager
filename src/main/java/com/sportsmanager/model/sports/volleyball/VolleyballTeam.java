package com.sportsmanager.model.sports.volleyball;

import com.sportsmanager.model.common.Lineup;
import com.sportsmanager.model.common.Team;

public class VolleyballTeam extends Team {

    public VolleyballTeam(String name) {
        super(name);
    }

    @Override
    public boolean isValidLineup(Lineup lineup) {
        return lineup != null && lineup.getStarters().size() == 6;
    }
}