package com.playersselectioniapplication;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.jboss.jandex.Main;

import com.playersselectioniapplication.model.Player;
import com.playersselectioniapplication.model.Score;
import com.playersselectioniapplication.repository.PlayerDAO;
import com.playersselectioniapplication.repository.PlayerDAOImpl;
import com.playersselectioniapplication.repository.ScoreDAO;
import com.playersselectioniapplication.repository.ScoreDAOImpl;

public class PlayersSelectionApplication {
	private static PlayerDAO playerDAO;
	private static ScoreDAO scoreDAO;

	public static void main(String[] args) {
		try {
			InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties");
			Properties props = new Properties();
			props.load(input);
			String url = props.getProperty("db.url");
			String username = props.getProperty("db.username");
			String password = props.getProperty("db.password");

			createDatabaseIfNotExists(url, username, password);
			createTablesIfNotExists(url, username, password);

			playerDAO = new PlayerDAOImpl();
			scoreDAO = new ScoreDAOImpl();

			showOptions();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createDatabaseIfNotExists(String url, String username, String password) throws SQLException {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement()) {

			String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS your_database_name";
			statement.executeUpdate(createDatabaseQuery);
		}
	}

	private static void createTablesIfNotExists(String url, String username, String password) throws SQLException {
		String createPlayerTableQuery = "CREATE TABLE IF NOT EXISTS Player (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "name VARCHAR(10) NOT NULL," + "domesticTeam VARCHAR(255) NOT NULL,"
				+ "average INT NOT NULL DEFAULT 0" + ")";

		String createScoreTableQuery = "CREATE TABLE IF NOT EXISTS Score (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "score INT NOT NULL," + "playerId INT NOT NULL," + "FOREIGN KEY (playerId) REFERENCES Player(id)"
				+ ")";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement()) {

			statement.executeUpdate(createPlayerTableQuery);
			statement.executeUpdate(createScoreTableQuery);
		}
	}

	private static void showOptions() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		int option = -1;

		while (option != 0) {
			System.out.println("Select an option:");
			System.out.println("1. Add a player");
			System.out.println("2. Add a score");
			System.out.println("3. Update player");
			System.out.println("4. Update score");
			System.out.println("5. Delete player");
			System.out.println("6. Delete score");
			System.out.println("7. Show all players");
			System.out.println("8. Show all scores");
			System.out.println("9. Search players by name");
			System.out.println("10. Search players by domestic team");
			System.out.println("11. Get scores by player ID");
			System.out.println("12. Get average of last three scores for a player");
			System.out.println("0. Exit");

			try {
				option = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character
			} catch (InputMismatchException e) {
				System.out.println("Invalid input!");
				scanner.nextLine(); // Consume the invalid input
				continue;
			}

			switch (option) {
			case 1:
				addPlayer(scanner);
				break;
			case 2:
				addScore(scanner);
				break;
			case 3:
				updatePlayer(scanner);
				break;
			case 4:
				updateScore(scanner);
				break;
			case 5:
				deletePlayer(scanner);
				break;
			case 6:
				deleteScore(scanner);
				break;
			case 7:
				showAllPlayers();
				break;
			case 8:
				showAllScores();
				break;
			case 9:
				searchPlayersByName(scanner);
				break;
			case 10:
				searchPlayersByDomesticTeam(scanner);
				break;
			case 11:
				getScoresByPlayerId(scanner);
				break;
			case 12:
				getAverageOfLastThreeScores(scanner);
				break;
			case 0:
				System.out.println("Exiting the application.");
				break;
			default:
				System.out.println("Invalid option!");
				break;
			}
		}

		scanner.close();
	}

	private static void addPlayer(Scanner scanner) throws SQLException {
		System.out.println("Enter player details:");
		System.out.print("Name: ");
		String name = scanner.nextLine();
		System.out.print("Domestic Team: ");
		String domesticTeam = scanner.nextLine();

		Player player = new Player(name, domesticTeam);
		playerDAO.addPlayer(player);
		System.out.println("Player added successfully.");
	}

	private static void addScore(Scanner scanner) throws SQLException {
		System.out.println("Enter score details:");
		System.out.print("Player ID: ");
		int playerId = scanner.nextInt();
		System.out.print("Score: ");
		int score = scanner.nextInt();

		Score scoreObj = new Score(playerId, score);
		scoreDAO.addScore(scoreObj);
		System.out.println("Score added successfully.");
	}

	private static void updatePlayer(Scanner scanner) throws SQLException {
		System.out.print("Enter the player ID to update: ");
		int playerId = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		Player player = playerDAO.getPlayerById(playerId);

		if (player == null) {
			System.out.println("Player not found!");
			return;
		}

		System.out.println("Current player details:");
		System.out.println(player);

		System.out.print("Enter new name (leave blank to keep the current value): ");
		String newName = scanner.nextLine();
		System.out.print("Enter new domestic team (leave blank to keep the current value): ");
		String newDomesticTeam = scanner.nextLine();

		if (!newName.isEmpty()) {
			player.setName(newName);
		}

		if (!newDomesticTeam.isEmpty()) {
			player.setDomesticTeam(newDomesticTeam);
		}

		playerDAO.updatePlayer(player);
		System.out.println("Player updated successfully.");
	}

	private static void updateScore(Scanner scanner) throws SQLException {
		System.out.print("Enter the score ID to update: ");
		int scoreId = scanner.nextInt();
		scanner.nextLine(); // Consume the newline character

		Score score = scoreDAO.getScoreById(scoreId);

		if (score == null) {
			System.out.println("Score not found!");
			return;
		}

		System.out.println("Current score details:");
		System.out.println(score);

		System.out.print("Enter new score (leave blank to keep the current value): ");
		String newScoreStr = scanner.nextLine();

		if (!newScoreStr.isEmpty()) {
			int newScore = Integer.parseInt(newScoreStr);
			score.setScore(newScore);
		}

		scoreDAO.updateScore(score);
		System.out.println("Score updated successfully.");
	}

	private static void deletePlayer(Scanner scanner) throws SQLException {
		System.out.print("Enter the player ID to delete: ");
		int playerId = scanner.nextInt();

		Player player = playerDAO.getPlayerById(playerId);

		if (player == null) {
			System.out.println("Player not found!");
			return;
		}

		playerDAO.deletePlayer(player);
		System.out.println("Player deleted successfully.");
	}

	private static void deleteScore(Scanner scanner) throws SQLException {
		System.out.print("Enter the score ID to delete: ");
		int scoreId = scanner.nextInt();

		Score score = scoreDAO.getScoreById(scoreId);

		if (score == null) {
			System.out.println("Score not found!");
			return;
		}

		scoreDAO.deleteScore(score);
		System.out.println("Score deleted successfully.");
	}

	private static void showAllPlayers() throws SQLException {
		List<Player> players = playerDAO.getAllPlayers();

		if (players.isEmpty()) {
			System.out.println("No players found!");
		} else {
			System.out.println("All players:");
			for (Player player : players) {
				System.out.println(player);
			}
		}
	}

	private static void showAllScores() throws SQLException {
		List<Score> scores = scoreDAO.getAllScores();

		if (scores.isEmpty()) {
			System.out.println("No scores found!");
		} else {
			System.out.println("All scores:");
			for (Score score : scores) {
				System.out.println(score);
			}
		}
	}

	private static void searchPlayersByName(Scanner scanner) throws SQLException {
		System.out.println("Enter player name to search:");
		String name = scanner.nextLine();

		List<Player> players = playerDAO.searchPlayersByName(name);

		if (!players.isEmpty()) {
			System.out.println("Players matching the search criteria:");
			for (Player player : players) {
				System.out.println(player);
			}
		} else {
			System.out.println("No players found matching the search criteria.");
		}
	}

	private static void searchPlayersByDomesticTeam(Scanner scanner) throws SQLException {
		System.out.println("Enter domestic team name to search:");
		String domesticTeam = scanner.nextLine();

		List<Player> players = playerDAO.searchPlayersByDomesticTeam(domesticTeam);

		if (!players.isEmpty()) {
			System.out.println("Players matching the search criteria:");
			for (Player player : players) {
				System.out.println(player);
			}
		} else {
			System.out.println("No players found matching the search criteria.");
		}
	}

	private static void getScoresByPlayerId(Scanner scanner) throws SQLException {
		System.out.println("Enter player ID:");
		int playerId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		List<Score> scores = scoreDAO.getScoresByPlayerId(playerId);

		if (!scores.isEmpty()) {
			System.out.println("Scores for player ID " + playerId + ":");
			for (Score score : scores) {
				System.out.println(score);
			}
		} else {
			System.out.println("No scores found for player ID " + playerId + ".");
		}
	}

	private static void getAverageOfLastThreeScores(Scanner scanner) throws SQLException {
		System.out.println("Enter player ID:");
		int playerId = scanner.nextInt();
		scanner.nextLine(); // Consume newline character

		double average = scoreDAO.getAverageOfLastThreeScores(playerId);
		System.out.println("Average of last three scores for player ID " + playerId + ": " + average);
	}
}
