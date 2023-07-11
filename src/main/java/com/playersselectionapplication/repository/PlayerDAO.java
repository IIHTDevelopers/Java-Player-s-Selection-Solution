package com.playersselectionapplication.repository;

import java.sql.SQLException;
import java.util.List;

import com.playersselectionapplication.model.Player;

public interface PlayerDAO {
	void addPlayer(Player player) throws SQLException;

	void updatePlayer(Player player) throws SQLException;

	int deletePlayer(Player player) throws SQLException;

	Player getPlayerById(int id) throws SQLException;

	List<Player> getAllPlayers() throws SQLException;

	List<Player> searchPlayersByName(String name) throws SQLException;

	List<Player> searchPlayersByDomesticTeam(String domesticTeam) throws SQLException;
}
