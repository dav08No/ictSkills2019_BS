package com.example.appdev_nocito_davide;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ParticipantDialogController implements DataReceiver {

    @FXML
    private TextField nameField;

    @FXML
    private RadioButton playerRadio;

    @FXML
    private RadioButton teamRadio;

    @FXML
    private CheckBox temporaryCheck;

    @FXML
    private Label errorLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private ToggleGroup typeGroup;

    private Participant currentParticipant;
    private Tournament currentTournament;

    @FXML
    public void initialize() {
        typeGroup = new ToggleGroup();
        playerRadio.setToggleGroup(typeGroup);
        teamRadio.setToggleGroup(typeGroup);
        errorLabel.setText("");
    }

    @FXML
    void onCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        String name = nameField.getText().trim();
        boolean isTeam = typeGroup.getSelectedToggle() == teamRadio;
        boolean isTemporary = temporaryCheck.isSelected();

        if (currentParticipant == null) {
            Participant p = new Participant(0, name, isTemporary, isTeam);
            Integer newId = db.addParticipant(p);
            if (newId != null) {
                p.setID(newId);
                db.addParticipantToTournament(currentTournament.getTournamentID(), p.getID());
            }
        } else {
            currentParticipant.setName(name);
            currentParticipant.setTemporary(isTemporary);
            currentParticipant.setTeam(isTeam);

            db.updateParticipant(currentParticipant);
        }

        close();
    }

    private boolean validateForm() {
        errorLabel.setText("");

        String name = nameField.getText() == null ? "" : nameField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Name darf nicht leer sein.");
            return false;
        }

        if (typeGroup.getSelectedToggle() == null) {
            errorLabel.setText("Bitte Player oder Team wählen.");
            return false;
        }

        return true;
    }

    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void receiveData(Object o) {
        if (o instanceof Object[] data) {
            Tournament t = (Tournament) data[0];
            Participant p = (Participant) data[1];

            this.currentTournament = t;
            this.currentParticipant = p;
        } else if (o instanceof Tournament t) {
            this.currentTournament = t;
            this.currentParticipant = null;
        }

        if (currentParticipant == null) {
            nameField.clear();
            typeGroup.selectToggle(null);
            temporaryCheck.setSelected(false);
            errorLabel.setText("");
        } else {
            nameField.setText(currentParticipant.getName());
            temporaryCheck.setSelected(currentParticipant.isTemporary());
            if (currentParticipant.isTeam()) {
                typeGroup.selectToggle(teamRadio);
            } else {
                typeGroup.selectToggle(playerRadio);
            }
            errorLabel.setText("");
        }
    }
}
