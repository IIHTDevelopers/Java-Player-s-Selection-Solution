package com.playersselectioniapplication.repository;

import java.sql.SQLException;
import java.util.List;

import com.playersselectioniapplication.model.Score;

public interface ScoreDAO {
	void addScore(Score score) throws SQLException;

	void updateScore(Score score) throws SQLException;

	void deleteScore(Score score) throws SQLException;

	Score getScoreById(int id) throws SQLException;

	List<Score> getAllScores() throws SQLException;

	List<Score> getScoresByPlayerId(int playerId) throws SQLException;

	double getAverageOfLastThreeScores(int playerId) throws SQLException;
}
