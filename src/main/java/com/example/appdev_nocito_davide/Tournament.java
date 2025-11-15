package com.example.appdev_nocito_davide;

public class Tournament {
    private int tournamentID;
    private String tournamentTitle;
    private int GameID;
    private int Size;
    private int WinnerParticipantID;
    private int TournamentState;

    public Tournament(int tournamentID, String tournamentTitle, int gameID, int size, int winnerParticipantID, int tournamentState) {
        this.tournamentID = tournamentID;
        this.tournamentTitle = tournamentTitle;
        this.GameID = gameID;
        this.Size = size;
        this.WinnerParticipantID = winnerParticipantID;
        this.TournamentState = tournamentState;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getTournamentTitle() {
        return tournamentTitle;
    }

    public void setTournamentTitle(String tournamentTitle) {
        this.tournamentTitle = tournamentTitle;
    }

    public int getGameID() {
        return GameID;
    }

    public void setGameID(int gameID) {
        GameID = gameID;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public int getWinnerParticipantID() {
        return WinnerParticipantID;
    }

    public void setWinnerParticipantID(int winnerParticipantID) {
        WinnerParticipantID = winnerParticipantID;
    }

    public int getTournamentState() {
        return TournamentState;
    }

    public void setTournamentState(int tournamentState) {
        TournamentState = tournamentState;
    }
}
