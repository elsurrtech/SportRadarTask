package com.sportradar.test;

import com.sportradar.Match;
import com.sportradar.ScoreBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {

    private ScoreBoard scoreboard;

    @BeforeEach
    void setUp() {
        scoreboard = new ScoreBoard();
    }

    @Test
    @DisplayName("Should start a new match with 0-0 score")
    void shouldStartNewMatch() {
        Match match = scoreboard.startMatch("Germany", "France");
        assertNotNull(match);
        assertEquals("Germany", match.getHomeTeam());
        assertEquals("France", match.getAwayTeam());
        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
        assertEquals(1, scoreboard.getMatchesCount());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if starting a match with same teams already in progress")
    void shouldThrowExceptionWhenStartingDuplicateMatch() {
        scoreboard.startMatch("Germany", "France");
        assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch("Germany", "France"));
    }

    @Test
    @DisplayName("Should update score for an existing match")
    void shouldUpdateScore() {
        scoreboard.startMatch("Germany", "France");
        scoreboard.updateScore("Germany", "France", 1, 0);

        List<Match> summary = scoreboard.getSummary();
        assertEquals(1, summary.size());
        Match updatedMatch = summary.getFirst();
        assertEquals(1, updatedMatch.getHomeScore());
        assertEquals(0, updatedMatch.getAwayScore());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if updating score for a non-existent match")
    void shouldThrowExceptionWhenUpdatingNonExistentMatch() {
        assertThrows(IllegalArgumentException.class, () -> scoreboard.updateScore("Germany", "France", 1, 0));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if updating with negative scores")
    void shouldThrowExceptionWhenUpdatingWithNegativeScores() {
        scoreboard.startMatch("Germany", "France");
        assertThrows(IllegalArgumentException.class, () -> scoreboard.updateScore("Germany", "France", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> scoreboard.updateScore("Germany", "France", 1, -5));
    }

    @Test
    @DisplayName("Should finish an existing match")
    void shouldFinishMatch() {
        scoreboard.startMatch("Germany", "France");
        assertEquals(1, scoreboard.getMatchesCount());

        scoreboard.finishMatch("Germany", "France");
        assertEquals(0, scoreboard.getMatchesCount());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if finishing a non-existent match")
    void shouldThrowExceptionWhenFinishingNonExistentMatch() {
        assertThrows(IllegalArgumentException.class, () -> scoreboard.finishMatch("Germany", "France"));
    }

    @Test
    @DisplayName("Should return empty list if no matches are in progress")
    void shouldReturnEmptySummaryWhenNoMatches() {
        List<Match> summary = scoreboard.getSummary();
        assertTrue(summary.isEmpty());
    }

    @Test
    @DisplayName("Should return summary ordered by total score (desc) and then by most recent start time (desc)")
    void shouldReturnOrderedSummary() throws InterruptedException {
        // Example matches as per requirements
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore("Mexico", "Canada", 0, 5); // Total 5

        // Introduce a slight delay to ensure distinct start times for matches started close together
        Thread.sleep(10);
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.updateScore("Spain", "Brazil", 10, 2); // Total 12

        Thread.sleep(10);
        scoreboard.startMatch("Germany", "France");
        scoreboard.updateScore("Germany", "France", 2, 2); // Total 4

        Thread.sleep(10);
        scoreboard.startMatch("Uruguay", "Italy");
        scoreboard.updateScore("Uruguay", "Italy", 6, 6); // Total 12

        Thread.sleep(10);
        scoreboard.startMatch("Argentina", "Australia");
        scoreboard.updateScore("Argentina", "Australia", 3, 1); // Total 4

        List<Match> summary = scoreboard.getSummary();
        System.out.println(summary);
        assertEquals(5, summary.size());

        // Expected order:
        // 1. Uruguay 6 - Italy 6 (Total 12, latest start)
        // 2. Spain 10 - Brazil 2 (Total 12, earlier start)
        // 3. Mexico 0 - Canada 5 (Total 5)
        // 4. Argentina 3 - Australia 1 (Total 4, latest start)
        // 5. Germany 2 - France 2 (Total 4, earlier start)

        assertEquals("Uruguay", summary.getFirst().getHomeTeam());
        assertEquals("Italy", summary.getFirst().getAwayTeam());
        assertEquals(6, summary.get(0).getHomeScore());
        assertEquals(6, summary.get(0).getAwayScore());

        assertEquals("Spain", summary.get(1).getHomeTeam());
        assertEquals("Brazil", summary.get(1).getAwayTeam());
        assertEquals(10, summary.get(1).getHomeScore());
        assertEquals(2, summary.get(1).getAwayScore());

        assertEquals("Mexico", summary.get(2).getHomeTeam());
        assertEquals("Canada", summary.get(2).getAwayTeam());
        assertEquals(0, summary.get(2).getHomeScore());
        assertEquals(5, summary.get(2).getAwayScore());

        assertEquals("Argentina", summary.get(3).getHomeTeam());
        assertEquals("Australia", summary.get(3).getAwayTeam());
        assertEquals(3, summary.get(3).getHomeScore());
        assertEquals(1, summary.get(3).getAwayScore());

        assertEquals("Germany", summary.get(4).getHomeTeam());
        assertEquals("France", summary.get(4).getAwayTeam());
        assertEquals(2, summary.get(4).getHomeScore());
        assertEquals(2, summary.get(4).getAwayScore());

        // Verify the order for same total scores
        assertTrue(summary.get(0).getStartTime().isAfter(summary.get(1).getStartTime()));
        assertTrue(summary.get(3).getStartTime().isAfter(summary.get(4).getStartTime()));
    }

    @Test
    @DisplayName("Match toString format")
    void matchToStringFormat() {
        Match match = new Match("TestHome", "TestAway");
        match.updateScore(2, 1);
        assertEquals("TestHome 2 - TestAway 1", match.toString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if team names are null or empty")
    void shouldThrowExceptionForInvalidTeamNames() {
        assertThrows(IllegalArgumentException.class, () -> new Match(null, "Away"));
        assertThrows(IllegalArgumentException.class, () -> new Match("Home", ""));
        assertThrows(IllegalArgumentException.class, () -> new Match(" ", "Away"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if home and away teams are the same")
    void shouldThrowExceptionForSameTeams() {
        assertThrows(IllegalArgumentException.class, () -> new Match("TeamA", "TeamA"));
    }
}
