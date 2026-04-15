package com.sportsmanager;

import com.sportsmanager.core.GameManager;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.IMatchEngine;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.League;
import com.sportsmanager.model.common.Match;
import com.sportsmanager.model.sports.football.FootballCoach;
import com.sportsmanager.model.sports.football.FootballPlayer;
import com.sportsmanager.model.sports.football.FootballTeam;

public class Main {
    public static void main(String[] args) {
        ISport sport = SportFactory.createSport("football");
        IMatchEngine engine = SportFactory.createEngine("football");

        System.out.println("Sport: " + sport.getSportName());
        System.out.println("Number of periods: " + sport.getNumberOfPeriods());
        System.out.println("Squad size: " + sport.getSquadSize());

        FootballTeam home = new FootballTeam("Galatasaray");
        FootballTeam away = new FootballTeam("Fenerbahce");

        for (int i = 0; i < 11; i++) {
            home.addPlayer(new FootballPlayer("Home Player " + i, 75));
            away.addPlayer(new FootballPlayer("Away Player " + i, 72));
        }

        home.addCoach(new FootballCoach("Okan Buruk", 10));
        away.addCoach(new FootballCoach("Mourinho", 20));

        Match match = new Match(home, away);

        League league = new League("Super League");
        league.addTeam(home);
        league.addTeam(away);

        GameManager gm = new GameManager(sport, engine, league);
        gm.playMatch(match);
    }
}//yusuf