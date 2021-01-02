package games.CutBomb.dto;

import games.CutBomb.model.Game;

import java.util.Date;

public class GameDTO {
    private long id;
    private Date created;
    private int occupancy;
    private int capacity;
    private String state;
    private boolean alreadyIn;

    public GameDTO(Game game){
        this.id = game.getId();
        this.created = game.getCreated();
        this.occupancy = game.numberOfPlayers();
        this.capacity = game.getCapacity();
        this.state = createState(game);
    }

    public long getId() { return id; }
    public Date getCreated() { return created; }
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
