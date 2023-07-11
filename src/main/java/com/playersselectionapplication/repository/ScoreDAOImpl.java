package com.playersselectionapplication.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.playersselectionapplication.model.Score;

public class ScoreDAOImpl implements ScoreDAO {
	private final String url;
	private final String username;
	private final String password;

	public ScoreDAOImpl() {
		Properties properties = new Properties();
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
			if (inputStream == null) {
				throw new RuntimeException("application.properties file not found");
			}
			properties.load(inputStream);
			url = properties.getProperty("db.url");
			username = properties.getProperty("db.username");
			password = properties.getProperty("db.password");
		} catch (IOException e) {
			throw new RuntimeException("Failed to load application.properties file", e);
		}
	}

	@Override
	public void addScore(Score score) throws SQLException {
		String query = "INSERT INTO Score (playerId, score) VALUES (?, ?)";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, score.getPlayerId());
			statement.setInt(2, score.getScore());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Adding score failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					score.setId(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Adding score failed, no ID obtained.");
				}
			}
		}
	}

	@Override
	public void updateScore(Score score) throws SQLException {
		String query = "UPDATE Score SET score = ? WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, score.getScore());
			statement.setInt(2, score.getId());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Updating score failed, no rows affected.");
			}
		}
	}

	@Override
	public void deleteScore(Score score) throws SQLException {
		String query = "DELETE FROM Score WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, score.getId());

			int affectedRows = statement.executeUpdate();

//			if (affectedRows == 0) {
//				throw new SQLException("Deleting score failed, no rows affected.");
//			}
		}
	}

	@Override
	public Score getScoreById(int id) throws SQLException {
		String query = "SELECT * FROM Score WHERE id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Score score = new Score();
					score.setId(resultSet.getInt("id"));
					score.setPlayerId(resultSet.getInt("playerId"));
					score.setScore(resultSet.getInt("score"));
					return score;
				}
			}
		}

		return null; // Score not found
	}

	@Override
	public List<Score> getAllScores() throws SQLException {
		String query = "SELECT * FROM Score";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			List<Score> scores = new ArrayList<>();

			while (resultSet.next()) {
				Score score = new Score();
				score.setId(resultSet.getInt("id"));
				score.setPlayerId(resultSet.getInt("playerId"));
				score.setScore(resultSet.getInt("score"));
				scores.add(score);
			}

			return scores;
		}
	}

	@Override
	public List<Score> getScoresByPlayerId(int playerId) throws SQLException {
		String query = "SELECT * FROM Score WHERE playerId = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, playerId);

			try (ResultSet resultSet = statement.executeQuery()) {
				List<Score> scores = new ArrayList<>();

				while (resultSet.next()) {
					Score score = new Score();
					score.setId(resultSet.getInt("id"));
					score.setPlayerId(resultSet.getInt("playerId"));
					score.setScore(resultSet.getInt("score"));
					scores.add(score);
				}

				return scores;
			}
		}
	}

	@Override
	public double getAverageOfLastThreeScores(int playerId) throws SQLException {
		String query = "SELECT score FROM Score WHERE playerId = ? ORDER BY id DESC LIMIT 3";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, playerId);

			try (ResultSet resultSet = statement.executeQuery()) {
				int sum = 0;
				int count = 0;

				while (resultSet.next()) {
					sum += resultSet.getInt("score");
					count++;
				}

				if (count > 0) {
					return (double) sum / count;
				} else {
					return 0.0;
				}
			}
		}
	}
}
