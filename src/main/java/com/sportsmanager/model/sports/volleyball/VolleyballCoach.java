package com.sportsmanager.model.sports.volleyball;

import com.sportsmanager.model.common.Coach;
import com.sportsmanager.model.common.Player;

public class VolleyballCoach extends Coach {


    public VolleyballCoach(String name, int experience) {
        super(name, experience);
    }

    
    @Override
    public void trainPlayer(Player player) {
        if (player instanceof VolleyballPlayer)   {
            VolleyballPlayer vp = (VolleyballPlayer) player;
            vp.setServing(vp.getServing() + experience);
            vp.setBlocking(vp.getBlocking() + experience);
        }

    }


}