package com.example.appdev_nocito_davide;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MatchCardController {

    @FXML
    private Label matchTitleLabel;

    @FXML
    private Label leftNameLabel;

    @FXML
    private Label rightNameLabel;

    @FXML
    private Button leftWButton;

    @FXML
    private Button leftLButton;

    @FXML
    private Button rightWButton;

    @FXML
    private Button rightLButton;

    @FXML
    private Label leftResultLabel;

    @FXML
    private Label rightResultLabel;

    @FXML
    private TextField commentField;

    private TournamentOverviewController parent;
    private Match match;

    private Integer leftId;
    private Integer rightId;

    public void setup(
            TournamentOverviewController parent,
            Match match,
            String title,
            Integer leftId,
            String leftName,
            Integer rightId,
            String rightName
    ) {
        this.parent = parent;
        this.match = match;
        this.leftId = leftId;
        this.rightId = rightId;

        matchTitleLabel.setText(title);

        leftNameLabel.setText(leftName != null ? leftName : "-");
        rightNameLabel.setText(rightName != null ? rightName : "-");

        // free win: kein rechter Spieler
        if (rightId == null) {
            rightWButton.setDisable(true);
            rightLButton.setDisable(true);
        }

        updateUI();
    }

    private void updateUI() {
        Integer winnerId = match.getWinnerParticipantID();

        boolean decided = winnerId != null;

        // Buttons nur sichtbar wenn noch nicht entschieden
        leftWButton.setVisible(!decided);
        leftLButton.setVisible(!decided);
        rightWButton.setVisible(!decided && rightId != null);
        rightLButton.setVisible(!decided && rightId != null);

        leftWButton.setManaged(!decided);
        leftLButton.setManaged(!decided);
        rightWButton.setManaged(!decided && rightId != null);
        rightLButton.setManaged(!decided && rightId != null);

        // Labels nur sichtbar wenn entschieden
        leftResultLabel.setVisible(decided);
        leftResultLabel.setManaged(decided);
        rightResultLabel.setVisible(decided);
        rightResultLabel.setManaged(decided);

        if (!decided) {
            leftResultLabel.setText("");
            rightResultLabel.setText("");
            commentField.setDisable(false);
            return;
        }

        // entschieden → W und L setzen
        if (winnerId.equals(leftId)) {
            leftResultLabel.setText("W");
            leftResultLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            rightResultLabel.setText("L");
            rightResultLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        } else if (rightId != null && winnerId.equals(rightId)) {
            leftResultLabel.setText("L");
            leftResultLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
            rightResultLabel.setText("W");
            rightResultLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            // Sicherheit, falls etwas schiefgelaufen ist
            leftResultLabel.setText("");
            rightResultLabel.setText("");
        }

        commentField.setDisable(false); // kannst du auch auf true setzen, wenn Kommentar nachher gesperrt sein soll
    }

    // Button Handler

    @FXML
    private void onLeftW() {
        if (leftId == null) {
            return;
        }
        setWinner(leftId);
    }

    @FXML
    private void onLeftL() {
        if (rightId == null) {
            return;
        }
        setWinner(rightId);
    }

    @FXML
    private void onRightW() {
        if (rightId == null) {
            return;
        }
        setWinner(rightId);
    }

    @FXML
    private void onRightL() {
        if (leftId == null) {
            return;
        }
        setWinner(leftId);
    }

    private void setWinner(Integer winnerId) {
        match.setWinnerParticipantID(winnerId);
        updateUI();

        if (parent != null && winnerId != null) {
            parent.onMatchResultUpdated(match, winnerId);
        }
    }
}
