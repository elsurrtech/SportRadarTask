package com.sportradar;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ScoreBoard scoreboard = new ScoreBoard();
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore("Mexico", "Canada", 0, 5); // Total 5

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

        for (Match m : summary) {
            System.out.println(m);
        }
    }
}