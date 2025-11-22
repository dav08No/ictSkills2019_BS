package com.example.appdev_nocito_davide;

import java.sql.*;
import java.util.ArrayList;

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

    public static ArrayList<Participant> getParticipants() {
        ArrayList<Participant> list = new ArrayList<>();

        String sql = "SELECT * FROM participant";

        try (PreparedStatement ps = connect().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

    public static ArrayList<Participant> getParticipantsForTournament(int tournamentID) {
        ArrayList<Participant> list = new ArrayList<>();

        String sql = """
                SELECT p.ID, p.Name, p.IsTemporary, p.IsTeam
                FROM participantintournament tp
                JOIN participant p ON p.ID = tp.ParticipantID
                WHERE tp.TournamentID = ?
                """;

        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tournamentID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("Name");
                    boolean isTemporary = rs.getBoolean("IsTemporary");
                    boolean isTeam = rs.getBoolean("IsTeam");

                    list.add(new Participant(id, name, isTemporary, isTeam));
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Integer addParticipant(Participant p) {
        String sql = "INSERT INTO participant (Name, IsTemporary, IsTeam) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connect().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getName());
            ps.setBoolean(2, p.isTemporary());
            ps.setBoolean(3, p.isTeam());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addParticipantToTournament(int tournamentID, int participantID) {
        String sql = "INSERT INTO participantintournament (TournamentID, ParticipantID) VALUES (?, ?)";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setInt(1, tournamentID);
            ps.setInt(2, participantID);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateParticipant(Participant p) {
        String sql = "UPDATE participant SET Name = ?, IsTemporary = ?, IsTeam = ? WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setBoolean(2, p.isTemporary());
            ps.setBoolean(3, p.isTeam());
            ps.setInt(4, p.getID());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeParticipantFromTournament(int tournamentID, int participantID) {
        String sql = "DELETE FROM participantintournament WHERE TournamentID = ? AND ParticipantID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setInt(1, tournamentID);
            ps.setInt(2, participantID);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteParticipant(int participantID) {
        String sql = "DELETE FROM participant WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setInt(1, participantID);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Match> getMatchesForTournament(int tournamentId) {
        ArrayList<Match> result = new ArrayList<>();
        String sql = "SELECT * FROM `match` WHERE TournamentID = ? ORDER BY Stage, `Order`";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ID");
                int tId = rs.getInt("TournamentID");
                int p1 = rs.getInt("Participant1ID");
                Integer p2 = rs.getObject("Participant2ID", Integer.class);
                int stage = rs.getInt("Stage");
                int order = rs.getInt("Order");
                Integer winner = rs.getObject("WinnerParticipantID", Integer.class);

                Match m = new Match(id, tId, p1, p2, stage, order, winner);
                result.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Match> getMatchesForStage(int tournamentId, int stage) {
        ArrayList<Match> matches = new ArrayList<>();

        String sql = """
            SELECT ID,
                   TournamentID,
                   Participant1ID,
                   Participant2ID,
                   Stage,
                   `Order`,
                   WinnerParticipantID
            FROM `match`
            WHERE TournamentID = ?
              AND Stage = ?
            ORDER BY `Order`
            """;

        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, stage);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Match m = new Match(
                            rs.getInt("ID"),
                            rs.getInt("TournamentID"),
                            rs.getInt("Participant1ID"),
                            (Integer) rs.getObject("Participant2ID"),
                            rs.getInt("Stage"),
                            rs.getInt("Order"),
                            (Integer) rs.getObject("WinnerParticipantID")
                    );
                    matches.add(m);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return matches;
    }

    public static int addMatch(Match m) {
        String sql = """
            INSERT INTO `match`
            (TournamentID, Participant1ID, Participant2ID, Stage, `Order`, WinnerParticipantID)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connect().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getTournamentID());
            ps.setInt(2, m.getParticipant1ID());

            if (m.getParticipant2ID() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, m.getParticipant2ID());
            }

            ps.setInt(4, m.getStage());
            ps.setInt(5, m.getOrder());

            // hier auf null pruefen, nicht auf 0
            if (m.getWinnerParticipantID() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, m.getWinnerParticipantID());
            }

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                m.setID(id);
                return id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void updateMatchWinner(int matchId, int winnerId) {
        String sql = "UPDATE `match` SET WinnerParticipantID = ? WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setInt(1, winnerId);
            ps.setInt(2, matchId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> getWinnersForStage(int tournamentId, int stage) {
        ArrayList<Integer> winners = new ArrayList<>();

        String sql = """
            SELECT WinnerParticipantID
            FROM `match`
            WHERE TournamentID = ?
              AND Stage = ?
              AND WinnerParticipantID IS NOT NULL
            """;

        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, stage);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    winners.add(rs.getInt("WinnerParticipantID"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return winners;
    }

    public static void updateTournamentState(int tournamentID, int state) {

        String sql = "UPDATE tournament SET TournamentState = ? WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setInt(1, state);
            ps.setInt(2, tournamentID);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTournamentWinner(int tournamentID, int winnerID) {

        String sql = "UPDATE tournament SET WinnerParticipantID = ? WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setInt(1, winnerID);
            ps.setInt(2, tournamentID);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Participant getParticipantByID(int participantID) {
        String sql = "SELECT * FROM participant WHERE ID = ?";
        Participant p = null;
        try (PreparedStatement ps = connect().prepareStatement(sql)) {
            ps.setInt(1, participantID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                boolean isTemporary = rs.getBoolean("IsTemporary");
                boolean isTeam = rs.getBoolean("IsTeam");

                p = new Participant(id, name, isTemporary, isTeam);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static void updateTournamentWinnerAndState(int tournamentId, int winnerId, int state) {
        String sql = "UPDATE tournament SET WinnerParticipantID = ?, TournamentState = ? WHERE ID = ?";

        try (PreparedStatement ps = connect().prepareStatement(sql)) {

            ps.setInt(1, winnerId);
            ps.setInt(2, state);
            ps.setInt(3, tournamentId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
