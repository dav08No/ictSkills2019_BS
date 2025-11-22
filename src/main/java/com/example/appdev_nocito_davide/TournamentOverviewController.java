package com.example.appdev_nocito_davide;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private BorderPane matchesRoot;

    @FXML
    private Button startTournamentButton;

    @FXML
    private TabPane stagesTabPane;

    private Tournament currentTournament;
    private final ArrayList<Game> allGames = db.getGames();
    private final ObservableList<Participant> allParticipants = FXCollections.observableArrayList();


    // Flag: wurde das Turnier bereits gestartet
    private final BooleanProperty tournamentStarted = new SimpleBooleanProperty(false);

    private int nextNumber = 1;

    @FXML
    public void initialize() {
        participantNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        participantsList.setItems(allParticipants);

        participantsCount.textProperty().bind(Bindings.size(allParticipants).asString());

        participantsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> updateButtonStates());

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasTournament = currentTournament != null;
        boolean started = hasTournament && currentTournament.getTournamentState() >= 1;
        boolean full = hasTournament && allParticipants.size() >= currentTournament.getSize();
        boolean hasSelection = participantsList.getSelectionModel().getSelectedItem() != null;

        addParticipant.setDisable(!hasTournament || started || full);
        FillRandomButton.setDisable(!hasTournament || started || full);

        EditButton.setDisable(!hasTournament || started || !hasSelection);
        RemoveButton.setDisable(!hasTournament || started || !hasSelection);
    }

    @FXML
    void onAddParticipant(ActionEvent event) {
        try {
            App.OpenDialog("ParticipantDialog", "Participant", 250, 350, new Object[]{currentTournament, null});
            loadParticipants(); // ruft selber updateButtonStates()
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
        updateButtonStates();
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
        updateButtonStates();
    }

    @FXML
    void onExportData(ActionEvent event) {
        // optional
    }

    @FXML
    void onStartTournament(ActionEvent event) {
        if (allParticipants.size() == currentTournament.getSize()) {
            db.updateTournamentState(this.currentTournament.getTournamentID(), 1);
            currentTournament.setTournamentState(1);
            tournamentStarted.set(true);

            createStage(1);
            loadStages();
            updateButtonStates();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Start Tournament");
            alert.setHeaderText(null);
            alert.setContentText("The tournament cannot be started until it is full.");
            alert.showAndWait();
        }
    }

    private void createStage(int stageNumber) {
        ArrayList<Participant> players = new ArrayList<>();

        if (stageNumber == 1) {
            players.addAll(allParticipants);
        } else {
            int previousStage = stageNumber - 1;
            ArrayList<Integer> winnerIds = db.getWinnersForStage(currentTournament.getTournamentID(), previousStage);

            for (Integer id : winnerIds) {
                Participant p = db.getParticipantByID(id);
                if (p != null) {
                    players.add(p);
                }
            }
        }

        if (players.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot create new stage");
            alert.setHeaderText(null);
            alert.setContentText("No players available to create matches for stage " + stageNumber + ".");
            alert.showAndWait();
            return;
        }

        java.util.Collections.shuffle(players);

        int matchOrder = 1;

        for (int i = 0; i + 1 < players.size(); i += 2) {
            Participant p1 = players.get(i);
            Participant p2 = players.get(i + 1);

            Match m = new Match(0, currentTournament.getTournamentID(), p1.getID(), p2.getID(), stageNumber, matchOrder, null);

            db.addMatch(m);
            matchOrder++;
        }

        if (players.size() % 2 == 1) {
            Participant last = players.get(players.size() - 1);

            Match freeWinMatch = new Match(0, currentTournament.getTournamentID(), last.getID(), null, stageNumber, matchOrder, last.getID());

            db.addMatch(freeWinMatch);
        }
    }

    private void loadStages() {
        if (stagesTabPane == null) {
            return;
        }

        stagesTabPane.getTabs().clear();

        ArrayList<Match> matches = db.getMatchesForTournament(currentTournament.getTournamentID());

        if (matches.isEmpty()) {
            startTournamentButton.setVisible(true);
            startTournamentButton.setManaged(true);
            stagesTabPane.setVisible(false);
            stagesTabPane.setManaged(false);
            return;
        }

        startTournamentButton.setVisible(false);
        startTournamentButton.setManaged(false);
        stagesTabPane.setVisible(true);
        stagesTabPane.setManaged(true);

        int maxStage = 1;
        for (Match m : matches) {
            if (m.getStage() > maxStage) {
                maxStage = m.getStage();
            }
        }

        for (int stageNumber = 1; stageNumber <= maxStage; stageNumber++) {

            ArrayList<Match> stageMatches = new ArrayList<>();
            for (Match m : matches) {
                if (m.getStage() == stageNumber) {
                    stageMatches.add(m);
                }
            }

            Tab stageTab = new Tab("Stage " + stageNumber);

            VBox stageRoot = new VBox(8);
            stageRoot.setPadding(new Insets(8));

            Label stageTitle = new Label("Stage " + stageNumber);
            stageTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            int matchCount = stageMatches.size();
            Set<Integer> participantIds = new HashSet<>();
            for (Match m : stageMatches) {
                participantIds.add(m.getParticipant1ID());
                if (m.getParticipant2ID() != null) {
                    participantIds.add(m.getParticipant2ID());
                }
            }
            int participantCount = participantIds.size();

            Label infoLabel = new Label(matchCount + " Matches    " + participantCount + " Participants");

            VBox headerBox = new VBox(4, stageTitle, infoLabel);
            stageRoot.getChildren().add(headerBox);

            VBox matchesBox = new VBox(8);
            for (Match m : stageMatches) {
                Node card = createMatchCardNode(m);
                matchesBox.getChildren().add(card);
            }

            ScrollPane scroll = new ScrollPane(matchesBox);
            scroll.setFitToWidth(true); // passt die Karten an die Breite an
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);

            stageRoot.getChildren().add(scroll);

            stageTab.setContent(stageRoot);
            stagesTabPane.getTabs().add(stageTab);

        }
    }

    private Node createMatchCardNode(Match m) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("MatchCard.fxml"));
            Node root = loader.load();

            MatchCardController controller = loader.getController();

            String title = "Match " + m.getStage() + "." + m.getOrder();
            String leftName = getParticipantNameById(m.getParticipant1ID());
            String rightName = getParticipantNameById(m.getParticipant2ID());

            controller.setup(
                    this,
                    m,
                    title,
                    m.getParticipant1ID(),
                    leftName,
                    m.getParticipant2ID(),
                    rightName
            );

            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return new Label("Error loading match");
        }
    }

    public void onMatchResultUpdated(Match match, Integer winnerId) {
        if (winnerId == null) {
            return;
        }

        db.updateMatchWinner(match.getID(), winnerId);

        checkStageFinished(match.getStage());
    }

    private void checkStageFinished(Integer stageNumber) {
        // alle Matches dieser Stage laden
        ArrayList<Match> stageMatches =
                db.getMatchesForStage(currentTournament.getTournamentID(), stageNumber);

        // wenn noch mindestens ein Match undecided ist, abbrechen
        for (Match m : stageMatches) {
            if (m.getWinnerParticipantID() == null) {
                return;
            }
        }

        // Gewinner dieser Stage holen (schon mit DISTINCT in db)
        ArrayList<Integer> winnerIds =
                db.getWinnersForStage(currentTournament.getTournamentID(), stageNumber);

        // Nur ein Gewinner = Turnier fertig
        if (winnerIds.size() == 1) {
            Integer winnerId = winnerIds.get(0);
            db.updateTournamentWinnerAndState(currentTournament.getTournamentID(), winnerId, 2);
            currentTournament.setWinnerParticipantID(winnerId);
            currentTournament.setTournamentState(2);
            tournamentStarted.set(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tournament finished");
            alert.setHeaderText(null);
            alert.setContentText("Winner: " + getParticipantNameById(winnerId));
            alert.showAndWait();
            return;
        }

        // Mehr als 1 Gewinner = neue Stage
        if (winnerIds.size() > 1) {
            int nextStage = stageNumber + 1;

            // NEU: prüfen ob es diese Stage schon gibt
            ArrayList<Match> nextStageMatches =
                    db.getMatchesForStage(currentTournament.getTournamentID(), nextStage);

            if (!nextStageMatches.isEmpty()) {
                // Stage existiert bereits, nur UI neu zeichnen
                loadStages();
                return;
            }

            // Stage noch nicht vorhanden, also anlegen
            createStage(nextStage);
            loadStages();
        }
    }

    private String getParticipantNameById(Integer id) {
        if (id == null) {
            return "";
        }

        if (allParticipants == null) {
            return "Unknown";
        }

        for (Participant p : allParticipants) {
            if (p.getID() == id) {
                return p.getName();
            }
        }

        return "Unknown";
    }

    private void loadParticipants() {
        allParticipants.setAll(db.getParticipantsForTournament(currentTournament.getTournamentID()));

        tournamentSize.setText(String.valueOf(currentTournament.getSize()));

        updateButtonStates();
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

            tournamentStarted.set(t.getTournamentState() >= 1);

            loadParticipants();
            loadStages();
            updateButtonStates();
        }
    }
}
