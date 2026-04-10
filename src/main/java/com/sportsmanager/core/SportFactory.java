package com.sportsmanager.core;

import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.sports.football.FootballMatchEngine;
import com.sportsmanager.model.sports.football.FootballSport;
public class SportFactory {
    public static ISport createSport(String sportType){
        if(sportType.equalsIgnoreCase("football")){
            return new FootballSport();
        }
        throw new IllegalArgumentException("Unknown sport: " +sportType);
    }
    public static IMatchEngine createEngine(String sportType){
        if(sportType.equalsIgnoreCase("football")){
            return new FootballMatchEngine();
        }
        throw new IllegalArgumentException("Unknown sport:" + sportType);
    }
    //Selim
}
