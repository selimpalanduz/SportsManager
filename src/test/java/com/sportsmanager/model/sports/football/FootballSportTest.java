package com.sportsmanager.model.sports.football;

import com.sportsmanager.model.sports.football.FootballSport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FootballSportTest {

    private FootballSport sport;

    @BeforeEach
    void setUp() {
        sport = new FootballSport();
    }

    @Test
    void sportNameShouldBeFootball() {
        assertEquals("Football", sport.getSportName());
    }

    @Test
    void shouldHaveTwoPeriods() {
        assertEquals(2, sport.getNumberOfPeriods());
    }

    @Test
    void periodDurationShouldBe45Minutes() {
        assertEquals(45, sport.getPeriodDurationMinutes());
    }

    @Test
    void winShouldGiveThreePoints() {
        assertEquals(3, sport.getPointsForWin());
    }

    @Test
    void drawShouldGiveOnePoint() {
        assertEquals(1, sport.getPointsForDraw());
    }

    @Test
    void squadSizeShouldBeEleven() {
        assertEquals(11, sport.getSquadSize());
    }

    @Test
    void allowedSubstitutionsShouldBeFive() {
        assertEquals(5, sport.getAllowedSubstitutions());
    }
}
