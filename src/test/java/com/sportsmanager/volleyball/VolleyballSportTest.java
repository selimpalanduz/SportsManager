package com.sportsmanager.volleyball;

import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.sports.volleyball.VolleyballSport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VolleyballSportTest {

    private ISport sport;

    @BeforeEach
    void setUp() {
        sport = new VolleyballSport();
    }

    @Test
    void name_isVolleyball() {
        assertEquals("Volleyball", sport.getSportName());
    }

    @Test
    void numberOfPeriods_is5() {
        assertEquals(5, sport.getNumberOfPeriods());
    }

    @Test
    void squadSize_is6() {
        assertEquals(6, sport.getSquadSize());
    }

    @Test
    void pointsForWin_is3() {
        assertEquals(3, sport.getPointsForWin());
    }

    @Test
    void pointsForDraw_is0() {
        assertEquals(0, sport.getPointsForDraw());
    }

    @Test
    void factoryProducesVolleyballSport() {
        ISport fromFactory = SportFactory.createSport("volleyball");
        assertNotNull(fromFactory);
        assertEquals("Volleyball", fromFactory.getSportName());
        assertEquals(5, fromFactory.getNumberOfPeriods());
    }

    @Test
    void factoryIsCaseInsensitive() {
        assertEquals("Volleyball", SportFactory.createSport("VOLLEYBALL").getSportName());
        assertEquals("Volleyball", SportFactory.createSport("Volleyball").getSportName());
    }
}