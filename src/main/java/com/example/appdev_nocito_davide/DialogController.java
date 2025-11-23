package com.example.appdev_nocito_davide;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class DialogController implements DataReceiver {

    @FXML
    private Label errorLabel;

    @FXML
    private GridPane formGrid;

    @FXML
    private ComboBox<Game> gameBox;

    @FXML
    private Label headerLabel;

    @FXML
    private VBox rootPane;

    @FXML
    private Spinner<Integer> sizeField;

    @FXML
    private Button submitButton;

    @FXML
    private TextField titleField;

    private ArrayList<Game> allGames;

    @FXML
    void onSaveTournament(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        String title = titleField.getText().trim();
        Game selectedGame = gameBox.getValue();
        int size = sizeField.getValue();

        if (currentTournament == null) {
            Tournament t = new Tournament(0, title, selectedGame.getID(), size, 0, 0);

            Integer id = db.addTournament(t);
            if (id != null) {
                t.setTournamentID(id);

            }
        } else {
            currentTournament.setTournamentTitle(title);
            currentTournament.setGameID(selectedGame.getID());
            currentTournament.setSize(size);

            db.updateTournament(currentTournament);
        }

        submitButton.getScene().getWindow().hide();
    }

    private boolean validateForm() {
        errorLabel.setText("");

        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        if (title.isEmpty()) {
            errorLabel.setText("Titel darf nicht leer sein.");
            return false;
        }

        Game selectedGame = gameBox.getValue();
        if (selectedGame == null) {
            errorLabel.setText("Bitte ein Game auswählen.");
            return false;
        }

        Integer size = sizeField.getValue();
        if (size == null || size < 2 || size > 100) {
            errorLabel.setText("Size muss zwischen 2 und 100 liegen.");
            return false;
        }

        return true;
    }

    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100);
        sizeField.setValueFactory(valueFactory);

        allGames = db.getGames();
        gameBox.setItems(FXCollections.observableArrayList(allGames));

        errorLabel.setText("");
    }

    private Tournament currentTournament;

    @Override
    public void receiveData(Object o) {
        if (o == null) {
            this.currentTournament = null;

            titleField.clear();
            gameBox.getSelectionModel().clearSelection();
            sizeField.getValueFactory().setValue(8);

            errorLabel.setText("");
            return;
        }

        if (o instanceof Tournament t) {
            this.currentTournament = t;

            titleField.setText(t.getTournamentTitle());
            sizeField.getValueFactory().setValue(t.getSize());

            Game selectedGame = null;

            for (Game game : allGames) {
                if (game.getID() == t.getGameID()) {
                    selectedGame = game;
                }
            }

            gameBox.setValue(selectedGame);

            errorLabel.setText("");
        }
    }
}
