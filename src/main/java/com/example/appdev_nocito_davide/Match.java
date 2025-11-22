package com.example.appdev_nocito_davide;

public class Match {
    private int ID;
    private int TournamentID;
    private int Participant1ID;
    private Integer Participant2ID;
    private int Stage;
    private int Order;
    private Integer WinnerParticipantID;

    public Match(int ID, int tournamentID, int participant1ID, Integer participant2ID, int stage, int order, Integer winnerParticipantID) {
        this.ID = ID;
        TournamentID = tournamentID;
        Participant1ID = participant1ID;
        Participant2ID = participant2ID;
        Stage = stage;
        Order = order;
        WinnerParticipantID = winnerParticipantID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTournamentID() {
        return TournamentID;
    }

    public void setTournamentID(int tournamentID) {
        TournamentID = tournamentID;
    }

    public int getParticipant1ID() {
        return Participant1ID;
    }

    public void setParticipant1ID(Integer participant1ID) {
        Participant1ID = participant1ID;
    }

    public Integer getParticipant2ID() {
        return Participant2ID;
    }

    public void setParticipant2ID(Integer participant2ID) {
        Participant2ID = participant2ID;
    }

    public int getStage() {
        return Stage;
    }

    public void setStage(int stage) {
        Stage = stage;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public Integer getWinnerParticipantID() {
        return WinnerParticipantID;
    }

    public void setWinnerParticipantID(Integer winnerParticipantID) {
        WinnerParticipantID = winnerParticipantID;
    }
}
