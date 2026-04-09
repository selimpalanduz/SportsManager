package com.sportsmanager.model.sports.football;

import com.sportsmanager.model.common.Coach;
import com.sportsmanager.model.common.Player;

public class FootballCoach extends Coach {
    private String favoriteFormation;

    public FootballCoach(String name, int experience) {
        super(name, experience);
        this.favoriteFormation = "4-4-2";
    }

    public FootballCoach(String name, int experience, String favoriteFormation) {
        super(name, experience);
        this.favoriteFormation = favoriteFormation;
    }

    @Override
    public void trainPlayer(Player player) {
        if (player instanceof FootballPlayer) {
            FootballPlayer fp = (FootballPlayer) player;
            fp.setPassing(fp.getPassing() + experience);
            fp.setShooting(fp.getShooting() + experience);
        }
    }

    public String getFavoriteFormation() { return favoriteFormation; }
}