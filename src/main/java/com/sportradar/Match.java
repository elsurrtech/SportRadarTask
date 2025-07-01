package com.sportradar;

import java.time.LocalDateTime;
import java.util.Objects;

public class Match {
    private final String homeTeam;
    private final String awayTeam;
    private int homeScore;
    private int awayScore;
    private final LocalDateTime startTime; // Make final for immutability once set

    // Original constructor for actual runtime usage
    public Match(String homeTeam, String awayTeam) {
        this(homeTeam, awayTeam, LocalDateTime.now()); // Call the new constructor with current time
    }

    // New constructor for controlled scenarios (e.g., testing)
    public Match(String homeTeam, String awayTeam, LocalDateTime startTime) {
        if (homeTeam == null || homeTeam.trim().isEmpty() || awayTeam == null || awayTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Team names cannot be null or empty.");
        }
        if (homeTeam.equalsIgnoreCase(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams cannot be the same.");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
        this.startTime = startTime; // Use the provided startTime
    }

    // ... (rest of the methods remain the same) ...

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void updateScore(int newHomeScore, int newAwayScore) {
        if (newHomeScore < 0 || newAwayScore < 0) {
            throw new IllegalArgumentException("Scores cannot be negative.");
        }
        this.homeScore = newHomeScore;
        this.awayScore = newAwayScore;
    }

    public int getTotalScore() {
        return homeScore + awayScore;
    }

    @Override
    public String toString() {
        return homeTeam + " " + homeScore + " - " + awayTeam + " " + awayScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        // Only compare teams for equality, not score or start time for identifying a match in progress
        return Objects.equals(homeTeam.toLowerCase(), match.homeTeam.toLowerCase()) &&
                Objects.equals(awayTeam.toLowerCase(), match.awayTeam.toLowerCase());
    }

    @Override
    public int hashCode() {
        // Hash code based on lowercased team names for consistent equals/hashCode contract
        return Objects.hash(homeTeam.toLowerCase(), awayTeam.toLowerCase());
    }
}