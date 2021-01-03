package games.CutBomb.dto;

import games.CutBomb.model.Game;

import java.util.Date;

import static games.CutBomb.Util.parseDate;

public class GameDTO {
    private long id;
    private String created;
    private String host;
    private int occupancy;
    private int capacity;
    private String state;
    private boolean alreadyIn;

    public GameDTO(Game game){
        this.id = game.getId();
        this.created = parseDate(game.getCreated());
        this.host = game.getHost().getPlayer().getUsername();
        this.occupancy = game.numberOfPlayers();
        this.capacity = game.getCapacity();
        this.state = createState(game);
    }

    public long getId() { return id; }
    public String getCreated() { return created; }
    public String getHost() { return host; }
    public int getOccupancy() { return occupancy; }
    public int getCapacity() { return capacity; }
    public String getState() { return state; }
    public boolean isAlreadyIn() { return alreadyIn; }
    public void setAlreadyIn(boolean alreadyIn) { this.alreadyIn = alreadyIn; }

    private String createState(Game game){
        if(game.getFinished() != null) return "finished";
        if(game.getStarted() != null) return "started";
        return "waiting";
    }
}
