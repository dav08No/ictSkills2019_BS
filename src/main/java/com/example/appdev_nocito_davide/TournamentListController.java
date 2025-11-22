package com.example.appdev_nocito_davide;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
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
            App.OpenDialog("Dialog", "Create Tournament", 500, 400, null);

            tournamentList.setAll(db.getTournaments());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onViewTournament() {
        Tournament selected = tournamentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            App.OpenDialog("TournamentOverview", "Tournament Overview", 800, 600, selected);

            tournamentList.setAll(db.getTournaments());
            tournamentTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Game> allGames = db.getGames();

    public void initialize() {
        setupTournamentTable();

        view.disableProperty().bind(tournamentTable.getSelectionModel().selectedItemProperty().isNull());
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

            Participant p = db.getParticipantByID(winnerId);

            String name;
            if (p != null) {
                name = p.getName();
            } else {
                name = "unknown";
            }

            return new ReadOnlyStringWrapper(name);
        });

        tournamentList = FXCollections.observableArrayList(db.getTournaments());
        tournamentTable.setItems(tournamentList);
    }
}
