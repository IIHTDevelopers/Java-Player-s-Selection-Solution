package com.assessment.playersselectionapplication.functional;

import static com.assessment.playersseelctionapplication.testutils.TestUtils.businessTestFile;
import static com.assessment.playersseelctionapplication.testutils.TestUtils.currentTest;
import static com.assessment.playersseelctionapplication.testutils.TestUtils.testReport;
import static com.assessment.playersseelctionapplication.testutils.TestUtils.yakshaAssert;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import com.playersselectioniapplication.model.Player;
import com.playersselectioniapplication.model.Score;
import com.playersselectioniapplication.repository.PlayerDAO;
import com.playersselectioniapplication.repository.PlayerDAOImpl;
import com.playersselectioniapplication.repository.ScoreDAO;
import com.playersselectioniapplication.repository.ScoreDAOImpl;

@Component
public class FunctionalTests {

	private static PlayerDAO playerDAO;
	private static ScoreDAO scoreDAO;
	private Player testPlayer;

	@BeforeAll
	static void setup() {
		playerDAO = new PlayerDAOImpl();
		scoreDAO = new ScoreDAOImpl();
	}

	@BeforeEach
	void clearDatabase() {
		try {
			List<Player> players = playerDAO.getAllPlayers();
			List<Score> scores = scoreDAO.getAllScores();
			for (Player player : players) {
				playerDAO.deletePlayer(player);
			}
			for (Score score : scores) {
				scoreDAO.deleteScore(score);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterAll
	public static void afterAll() {
		testReport();
	}

	@Test
	void addAddPlayerTest() throws IOException {
		Player player = new Player("John", "Team A");
		try {
			playerDAO.addPlayer(player);
			Player retrievedPlayer = playerDAO.getPlayerById(player.getId());
			try {
				yakshaAssert(currentTest(), player.getName().equals(retrievedPlayer.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void updateUpdatePlayerByIdTest() throws IOException {
		Player player = new Player("John", "Team A");
		try {
			playerDAO.addPlayer(player);
			player.setName("John Doe");
			player.setDomesticTeam("Team B");
			playerDAO.updatePlayer(player);
			Player updatedPlayer = playerDAO.getPlayerById(player.getId());
			try {
				yakshaAssert(currentTest(), player.getName().equals(updatedPlayer.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteDeletePlayerByIdTest() throws IOException {
		Player player = new Player("John", "Team A");
		try {
			playerDAO.addPlayer(player);
			int updatedRows = playerDAO.deletePlayer(player);
			Player retrievedPlayer = playerDAO.getPlayerById(player.getId());
			try {
				yakshaAssert(currentTest(), updatedRows > 0 && retrievedPlayer == null ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getGetAllPlayersTest() throws IOException {
		Player player1 = new Player("John", "Team A");
		Player player2 = new Player("Jane", "Team B");
		Player player3 = new Player("Mike", "Team C");
		try {
			playerDAO.addPlayer(player1);
			playerDAO.addPlayer(player2);
			playerDAO.addPlayer(player3);
			List<Player> players = playerDAO.getAllPlayers();
			try {
				yakshaAssert(currentTest(),
						players != null && players.size() == 3 && players.get(0).getName().equals(player1.getName())
								&& players.get(1).getName().equals(player2.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void searchSearchPlayersByNameTest() throws IOException {
		Player player1 = new Player("John", "Team A");
		Player player2 = new Player("Jane", "Team B");
		Player player3 = new Player("Mike", "Team C");
		try {
			playerDAO.addPlayer(player1);
			playerDAO.addPlayer(player2);
			playerDAO.addPlayer(player3);
			List<Player> searchResults = playerDAO.searchPlayersByName("John");
			try {
				yakshaAssert(currentTest(),
						searchResults != null && searchResults.size() == 1
								&& searchResults.get(0).getName().equals(player1.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void searchSearchPlayersByDomesticTeamTest() throws IOException {
		Player player1 = new Player("John", "Team A");
		Player player2 = new Player("Jane", "Team B");
		Player player3 = new Player("Mike", "Team C");
		try {
			playerDAO.addPlayer(player1);
			playerDAO.addPlayer(player2);
			playerDAO.addPlayer(player3);
			List<Player> searchResults = playerDAO.searchPlayersByDomesticTeam("Team B");
			try {
				yakshaAssert(currentTest(),
						searchResults != null && searchResults.size() == 1
								&& searchResults.get(0).getName().equals(player2.getName()) ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getGetPlayerByIdTest() throws IOException {
		try {
			testPlayer = new Player(10000, "John", "Team A", 56);
			try {
				playerDAO.addPlayer(testPlayer);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Player retrievedPlayer = playerDAO.getPlayerById(testPlayer.getId());
			try {
				yakshaAssert(currentTest(),
						retrievedPlayer != null && testPlayer.getName().equals(retrievedPlayer.getName()) ? true
								: false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void addAddScoreTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);
			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);
			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);
			Score retrievedScore = scoreDAO.getScoreById(scoreDAO.getAllScores().get(0).getId());
			try {
				yakshaAssert(currentTest(),
						retrievedScore != null && testScore1.getScore() == retrievedScore.getScore()
								&& testScore1.getPlayerId() == retrievedScore.getPlayerId() ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void updateUpdateScoreTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);
			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);
			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);
			Score retrievedScore = scoreDAO.getScoreById(scoreDAO.getAllScores().get(0).getId());
			retrievedScore.setScore(50);
			scoreDAO.updateScore(retrievedScore);
			Score updatedScore = scoreDAO.getScoreById(scoreDAO.getAllScores().get(0).getId());
			try {
				yakshaAssert(currentTest(), 50 == updatedScore.getScore() ? true : false, businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteDeleteScoreTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);
			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);
			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);
			scoreDAO.deleteScore(testScore1);
			Score retrievedScore = scoreDAO.getScoreById(testScore1.getId());
			try {
				yakshaAssert(currentTest(), retrievedScore == null ? true : false, businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getGetScoreByIdTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);
			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);
			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);
			Score retrievedScore = scoreDAO.getScoreById(testScore1.getId());
			try {
				yakshaAssert(currentTest(), testScore1.getScore() == retrievedScore.getScore() ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getGetAllScoresTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);
			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);
			Score testScore1 = new Score(testPlayer.getId(), 10);
			scoreDAO.addScore(testScore1);
			Score testScore2 = new Score(testPlayer.getId(), 20);
			scoreDAO.addScore(testScore2);
			List<Score> scores = scoreDAO.getAllScores();
			try {
				yakshaAssert(currentTest(),
						scores != null && scores.get(0).getScore() == testScore1.getScore() ? true : false,
						businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getGetScoresByPlayerIdTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);

			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);

			Score testScore1 = new Score(testPlayer.getId(), 10);
			Score testScore2 = new Score(testPlayer.getId(), 10);

			scoreDAO.addScore(testScore1);
			scoreDAO.addScore(testScore2);

			List<Score> scores = scoreDAO.getScoresByPlayerId(testPlayer.getId());
			try {
				yakshaAssert(currentTest(), scores != null && scores.size() == 2 ? true : false, businessTestFile);
			} catch (Exception e) {
				yakshaAssert(currentTest(), false, businessTestFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getGetAverageOfLastThreeScoresTest() throws IOException {
		try {
			testPlayer = new Player("John", "Team A");
			playerDAO.addPlayer(testPlayer);

			testPlayer = playerDAO.searchPlayersByName(testPlayer.getName()).get(0);

			if (testPlayer == null) {
				yakshaAssert(currentTest(), false, businessTestFile);
			} else {
				Score testScore1 = new Score(testPlayer.getId(), 10);
				Score testScore2 = new Score(testPlayer.getId(), 10);

				scoreDAO.addScore(testScore1);
				scoreDAO.addScore(testScore2);

				double average = scoreDAO.getAverageOfLastThreeScores(testPlayer.getId());
				try {
					yakshaAssert(currentTest(), average == 10 ? true : false, businessTestFile);
				} catch (Exception e) {
					yakshaAssert(currentTest(), false, businessTestFile);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
