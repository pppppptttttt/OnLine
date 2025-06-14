package ru.hse.online.GroupService.data;

public class Invite {
    public final String fromWho;
    public final String toWho;

    public Invite(String fromWho, String toWho) {
        this.fromWho = fromWho;
        this.toWho = toWho;
    }

    public String toJson() {
        return "{\"fromWho\": \"" + fromWho + "\", \"toWho\": \"" + toWho + "\"}";
    }
}
