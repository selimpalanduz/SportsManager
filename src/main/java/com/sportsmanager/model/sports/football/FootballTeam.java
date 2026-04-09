package com.sportsmanager.model.sports.football;

import com.sportsmanager.model.common.Lineup;
import com.sportsmanager.model.common.Team;

public class FootballTeam extends Team {

    public FootballTeam(String name) {
        super(name);
    }

    @Override
    public boolean isValidLineup(Lineup lineup) {
        return lineup != null && lineup.getStarters().size() == 11;
    }
}