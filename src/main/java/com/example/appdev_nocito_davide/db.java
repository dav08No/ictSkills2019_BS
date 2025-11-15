package com.example.appdev_nocito_davide;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class db {

    private static Connection connection;

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "ictskills";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        String url = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, USER, PASSWORD);
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    public static ArrayList<Tournament> getTournaments() {
        ArrayList<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournament";
        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int ID = rs.getInt("ID");
                String Title = rs.getString("Title");
                int GameID = rs.getInt("GameID");
                int Size = rs.getInt("Size");
                int WinnerParticipantID = rs.getInt("WinnerParticipantID");
                int TournamentState = rs.getInt("TournamentState");
                System.out.println("ID: " + ID + "; Title" + Title);
                tournaments.add(new Tournament(ID, Title, GameID, Size, WinnerParticipantID, TournamentState));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tournaments;
    }

    public static ArrayList<Game> getGames() {
        ArrayList<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM game";
        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int ID = rs.getInt("ID");
                String Name = rs.getString("Name");
                games.add(new Game(ID, Name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return games;
    }

    public static Integer addTournament(Tournament t) {
        String sql = "INSERT INTO tournament (Title, GameID, Size, WinnerParticipantID, TournamentState) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connect().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getTournamentTitle());
            ps.setInt(2, t.getGameID());
            ps.setInt(3, t.getSize());

            if (t.getWinnerParticipantID() == 0) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, t.getWinnerParticipantID());
            }

            ps.setInt(5, t.getTournamentState());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateTournament(Tournament t) {
        String sql = "UPDATE tournament SET Title = ?, GameID = ?, Size = ?, WinnerParticipantID = ?, TournamentState = ? WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setString(1, t.getTournamentTitle());
            ps.setInt(2, t.getGameID());
            ps.setInt(3, t.getSize());

            if (t.getWinnerParticipantID() == 0) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, t.getWinnerParticipantID());
            }

            ps.setInt(5, t.getTournamentState());
            ps.setInt(6, t.getTournamentID());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Participant> getParticipants() {
        List<Participant> list = new ArrayList<>();

        String sql = "SELECT ID, Name, IsTemporary, IsTeam FROM participant";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                boolean isTemporary = rs.getBoolean("IsTemporary");
                boolean isTeam = rs.getBoolean("IsTeam");

                list.add(new Participant(id, name, isTemporary, isTeam));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
