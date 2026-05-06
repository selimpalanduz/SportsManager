package com.sportsmanager.core;

import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.sports.football.FootballMatchEngine;
import com.sportsmanager.model.sports.football.FootballSport;
import com.sportsmanager.model.sports.volleyball.VolleyballMatchEngine;
import com.sportsmanager.model.sports.volleyball.VolleyballSport;
public class SportFactory {
    public static ISport createSport(String sportType){
        if(sportType.equalsIgnoreCase("football")){
            return new FootballSport();
        }
        if(sportType.equalsIgnoreCase("volleyball")){
            return new VolleyballSport();
        }
        throw new IllegalArgumentException("Unknown sport: " +sportType);
    }
    public static IMatchEngine createEngine(String sportType){
        if(sportType.equalsIgnoreCase("football")){
            return new FootballMatchEngine();
        }
        if(sportType.equalsIgnoreCase("volleyball")){
            return new VolleyballMatchEngine();
        }
        throw new IllegalArgumentException("Unknown sport:" + sportType);
    }
    //Selim
}
