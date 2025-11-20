package com.example.appdev_nocito_davide;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class TournamentOverviewController implements iReceiveData {

    @FXML
    private Button EditButton;

    @FXML
    private Tab Matches;

    @FXML
    private Tab Participants;

    @FXML
    private Button RemoveButton;

    @FXML
    private Button addParticipant;

    @FXML
    private Button exportButton;

    @FXML
    private Button FillRandomButton;

    @FXML
    private Label gameNameLabel;

    @FXML
    private Label participantsCount;

    @FXML
    private TableView<Participant> participantsList;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label underTitleLabel;

    @FXML
    private Label tournamentSize;

    @FXML
    private Label tournamentTitleLabel;

    @FXML
    private TableColumn<Participant, String> participantNameColumn;

    @FXML
    void onAddParticipant(ActionEvent event) {
        try {
            App.OpenDialog("ParticipantDialog", "Participant", 250, 350, new Object[]{currentTournament, null});

            loadParticipants();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        Participant selected = participantsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            App.OpenDialog("ParticipantDialog", "Participant", 250, 350, new Object[]{currentTournament, selected});

            loadParticipants();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int nextNumber = 1;

    @FXML
    void onFillRandom(ActionEvent event) {

        while (allParticipants.size() < currentTournament.getSize()) {
            String name = "Participant #" + nextNumber;
            Participant p = new Participant(0, name, true, true);

            Integer newId = db.addParticipant(p);
            if (newId != null) {
                p.setID(newId);
                db.addParticipantToTournament(currentTournament.getTournamentID(), p.getID());
                allParticipants.add(p);
            }

            nextNumber++;
        }

        updateAddButtonsDisabled();
    }

    @FXML
    void onRemove(ActionEvent event) {
        Participant selected = participantsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        db.removeParticipantFromTournament(currentTournament.getTournamentID(), selected.getID());

        if (selected.isTemporary()) {
            db.deleteParticipant(selected.getID());
        }

        allParticipants.remove(selected);
        updateAddButtonsDisabled();
    }

    @FXML
    void onExportData(ActionEvent event) {

    }

    private Tournament currentTournament;
    private final ArrayList<Game> allGames = db.getGames();
    private ObservableList<Participant> allParticipants;

    @FXML
    public void initialize() {
        participantNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        EditButton.disableProperty().bind(participantsList.getSelectionModel().selectedItemProperty().isNull());
        RemoveButton.disableProperty().bind(participantsList.getSelectionModel().selectedItemProperty().isNull());
    }

    private void loadParticipants() {
        allParticipants = FXCollections.observableArrayList(db.getParticipantsForTournament(currentTournament.getTournamentID()));

        participantsList.setItems(allParticipants);

        participantsCount.textProperty().bind(Bindings.size(allParticipants).asString());

        tournamentSize.setText(String.valueOf(currentTournament.getSize()));

        updateAddButtonsDisabled();
    }

    private void updateAddButtonsDisabled() {
        boolean full = allParticipants.size() >= currentTournament.getSize();
        addParticipant.setDisable(full);
        FillRandomButton.setDisable(full);
    }

    @Override
    public void receiveData(Object o) {
        if (o instanceof Tournament t) {
            this.currentTournament = t;

            tournamentTitleLabel.setText(t.getTournamentTitle());
            underTitleLabel.setText("Participants of \"" + t.getTournamentTitle() + "\"");

            String gameName = "Unknown";
            for (Game g : allGames) {
                if (g.getID() == t.getGameID()) {
                    gameName = g.getName();
                    break;
                }
            }
            gameNameLabel.setText(gameName);

            loadParticipants();
        }
    }
}
