package br.com.west.lojas.managers;

import br.com.west.lojas.enums.ChatType;
import br.com.west.lojas.models.Loja;
import br.com.west.lojas.models.PlayerMenuUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LojaManager {


    private final HashMap<String, Loja> loja = new HashMap<>();
    private final HashMap<String, ChatType> chat = new HashMap<>();
    private final HashMap<String, PlayerMenuUtility> playerMenuUtility = new HashMap<>();
    private final ArrayList<String> topVisits = new ArrayList<>();
    private final ArrayList<Loja> lojas = new ArrayList<>();
    private final HashSet<String> loadedPlayers = new HashSet<>();
    private final HashSet<String> activePlayers = new HashSet<>();


    public ArrayList<Loja> getLojas() {
        return lojas;
    }

    public HashMap<String, Loja> getLoja() {
        return loja;
    }

    public HashMap<String, ChatType> getChat() {
        return chat;
    }

    public HashSet<String> getLoadedPlayers() {
        return loadedPlayers;
    }

    public HashSet<String> getActivePlayers() {
        return activePlayers;
    }

    public HashMap<String, PlayerMenuUtility> getPlayerMenuUtility() {
        return playerMenuUtility;
    }

    public ArrayList<String> getTopVisits() {
        return topVisits;
    }

    public void loadLoja(String owner, Loja loja) {
        getLoja().put(owner, loja);
        getLoadedPlayers().add(owner);
    }

    public Loja getLoja(String playerName) {
        return loja.get(playerName);
    }

    public String getUserFormattedStatus(String playerName) {
        return getLoja(playerName).getStatus() ? "§a✔" : "§c✖";
    }

    public PlayerMenuUtility getPlayerMenuUtility(String viewer) {
        PlayerMenuUtility playerMenuUtility;
        if (!(getPlayerMenuUtility().containsKey(viewer))) {

            playerMenuUtility = new PlayerMenuUtility(viewer);
            getPlayerMenuUtility().put(viewer, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return getPlayerMenuUtility().get(viewer);
        }
    }
}
