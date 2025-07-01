package com.sportradar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * <p>
 * The {@code ScoreBoard} class provides a simple in-memory
 * scoreboard for tracking live football matches during a World Cup. It supports
 * operations such as starting new matches, updating scores, finishing matches,
 * and retrieving a summary of ongoing matches ordered by specific criteria.
 * </p>
 *
 * <h3>Usage Instructions:</h3>
 * <ol>
 * <li><b>Instantiation:</b> Create an instance of the scoreboard:
 * <pre>{@code
 * ScoreBoard scoreboard = new ScoreBoard();
 * }</pre>
 * </li>
 * <li><b>Starting a Match:</b> Use the {@code startMatch} method to add a new game.
 * The initial score will always be 0-0.
 * <pre>{@code
 * scoreboard.startMatch("Mexico", "Canada");
 * scoreboard.startMatch("Spain", "Brazil");
 * }</pre>
 * An {@code IllegalArgumentException} will be thrown if a match between the
 * same two teams is already in progress or if team names are invalid.
 * </li>
 * <li><b>Updating a Match Score:</b> Use the {@code updateScore} method to change
 * the scores for an ongoing match. Scores must be non-negative.
 * <pre>{@code
 * scoreboard.updateScore("Mexico", "Canada", 0, 5);
 * scoreboard.updateScore("Spain", "Brazil", 10, 2);
 * }</pre>
 * An {@code IllegalArgumentException} will be thrown if the match is not found
 * or if provided scores are negative.
 * </li>
 * <li><b>Finishing a Match:</b> Use the {@code finishMatch} method to remove a game
 * from the scoreboard once it's concluded.
 * <pre>{@code
 * scoreboard.finishMatch("Mexico", "Canada");
 * }</pre>
 * An {@code IllegalArgumentException} will be thrown if the match is not found.
 * </li>
 * <li><b>Getting a Summary:</b> Retrieve a list of all ongoing matches, sorted by
 * their total score (sum of home and away scores) in descending order.
 * For matches with the same total score, they are further sorted by
 * their start time, with the most recently started match appearing first.
 * <pre>{@code
 * List<Match> currentMatches = scoreboard.getSummary();
 * for (Match match : currentMatches) {
 * System.out.println(match.toString());
 * }
 * }</pre>
 * The returned list is unmodifiable.
 * </li>
 * </ol>
 *
 * <h3>Concurrency Notes:</h3>
 * <p>
 * This scoreboard uses a {@link java.util.concurrent.CopyOnWriteArrayList} internally
 * to store matches. This provides thread-safety for read operations (which are common
 * when getting summaries) and ensures that iterators do not throw {@code ConcurrentModificationException}
 * during modifications. While suitable for scenarios with many readers and few writers,
 * for very high write concurrency, alternative concurrent collections or explicit
 * synchronization might be considered if performance becomes a bottleneck.
 * </p>
 *
 * @see Match
 * @author Deepesh Sengar
 * @since 1.0
 */
public class ScoreBoard {
    // Using CopyOnWriteArrayList for thread-safety in a multi-threaded environment,
    // though for this simple in-memory solution, a regular ArrayList is also fine
    // as long as external synchronization is handled if used concurrently.
    private final List<Match> matchesInProgress;

    public ScoreBoard() {
        this.matchesInProgress = new CopyOnWriteArrayList<>();
    }

    /**
     * Starts a new match with initial score 0 - 0 and adds it to the scoreboard.
     *
     * @param homeTeam The name of the home team.
     * @param awayTeam The name of the away team.
     * @return The newly started Match object.
     * @throws IllegalArgumentException if team names are invalid or if a match with the same teams is already in progress.
     */
    public Match startMatch(String homeTeam, String awayTeam) {
        Match newMatch = new Match(homeTeam, awayTeam);
        // Check if a match with these teams is already in progress
        if (matchesInProgress.stream().anyMatch(m -> m.equals(newMatch))) {
            throw new IllegalArgumentException("A match between " + homeTeam + " and " + awayTeam + " is already in progress.");
        }
        matchesInProgress.add(newMatch);
        return newMatch;
    }

    /**
     * Updates the score of an ongoing match.
     *
     * @param homeTeam    The home team of the match to update.
     * @param awayTeam The away team of the match to update.
     * @param homeScore   The new score for the home team.
     * @param awayScore   The new score for the away team.
     * @throws IllegalArgumentException if scores are negative or if the match is not found.
     */
    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores cannot be negative.");
        }

        Optional<Match> matchToUpdate = matchesInProgress.stream()
                .filter(m -> m.getHomeTeam().equalsIgnoreCase(homeTeam) && m.getAwayTeam().equalsIgnoreCase(awayTeam))
                .findFirst();

        matchToUpdate.ifPresentOrElse(
                match -> match.updateScore(homeScore, awayScore),
                () -> { throw new IllegalArgumentException("Match " + homeTeam + " vs " + awayTeam + " not found."); }
        );
    }

    /**
     * Finishes a match currently in progress and removes it from the scoreboard.
     *
     * @param homeTeam The home team of the match to finish.
     * @param awayTeam The away team of the match to finish.
     * @throws IllegalArgumentException if the match is not found.
     */
    public void finishMatch(String homeTeam, String awayTeam) {
        boolean removed = matchesInProgress.removeIf(m -> m.getHomeTeam().equalsIgnoreCase(homeTeam) && m.getAwayTeam().equalsIgnoreCase(awayTeam));
        if (!removed) {
            throw new IllegalArgumentException("Match " + homeTeam + " vs " + awayTeam + " not found.");
        }
    }

    /**
     * Gets a summary of matches in progress, ordered by their total score (descending).
     * Matches with the same total score are ordered by the most recently started match.
     *
     * @return An unmodifiable list of matches in the specified order.
     */
    public List<Match> getSummary() {
        List<Match> sortedMatches = new ArrayList<>(matchesInProgress);
        List<Match> primarySort = sortedMatches.stream().sorted(Comparator.comparing(Match::getStartTime).reversed()).toList();
        return primarySort.stream().sorted(Comparator.comparing(Match::getTotalScore).reversed()).collect(Collectors.toList());
    }

    /**
     * Returns the current number of matches in progress.
     * @return The count of ongoing matches.
     */
    public int getMatchesCount() {
        return matchesInProgress.size();
    }
}