package br.com.west.lojas.models;

import br.com.west.lojas.Main;
import org.bukkit.Location;

public class Loja {

    private final String owner;
    private String announcement;
    private Location location;
    private int visits;
    private boolean status;

    public Loja(String owner, String announcement, Location location, int visits, boolean status) {
        this.owner = owner;
        this.announcement = announcement;
        this.location = location;
        this.visits = visits;
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
        Main.getInstance().getLojaManager().getActivePlayers().add(owner);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        Main.getInstance().getLojaManager().getActivePlayers().add(owner);
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
        Main.getInstance().getLojaManager().getActivePlayers().add(owner);
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        Main.getInstance().getLojaManager().getActivePlayers().add(owner);
    }
}
