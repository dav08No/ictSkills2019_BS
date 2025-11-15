package com.example.appdev_nocito_davide;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class TournamentListController {

    public static ObservableList<Tournament> tournamentList;

    @FXML
    private Button addTournament;

    @FXML
    private TableColumn<Tournament, String> title;
    @FXML
    private TableColumn<Tournament, String> game;

    @FXML
    private TableColumn<Tournament, String> winner;

    @FXML
    private TableView<Tournament> tournamentTable;

    @FXML
    private Button view;

    @FXML
    void onAddTournament() {
        try {
            App.OpenDialog("Dialog", "Create Tournament", 600, 300, null);

            tournamentList.setAll(db.getTournaments());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Game> allGames = db.getGames();
    private static List<Participant> allParticipants = db.getParticipants();

    public void initialize() {
        setupTournamentTable();
    }

    public void setupTournamentTable() {
        title.setCellValueFactory(new PropertyValueFactory<>("tournamentTitle"));
        game.setCellValueFactory(cellData -> {
            int gameId = cellData.getValue().getGameID();

            String title = "Unknown";

            for (Game g : allGames) {
                if (g.getID() == gameId) {
                    title = g.getName();
                    break;
                }
            }

            return new ReadOnlyStringWrapper(title);
        });

        winner.setCellValueFactory(cellData -> {
            Integer winnerId = cellData.getValue().getWinnerParticipantID();

            if (winnerId == null || winnerId == 0) {
                return new ReadOnlyStringWrapper("undecided");
            }

            String name = "unknown";

            for (Participant p : allParticipants) {
                if (p.getID() == winnerId) {
                    name = p.getName();
                    break;
                }
            }

            return new ReadOnlyStringWrapper(name);
        });




        tournamentList = FXCollections.observableArrayList(db.getTournaments());
        System.out.println(tournamentList.size());
        tournamentTable.setItems(tournamentList);
    }
}
