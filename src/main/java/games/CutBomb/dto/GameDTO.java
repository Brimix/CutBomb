package games.CutBomb.dto;

import games.CutBomb.model.Game;

import java.util.Date;

public class GameDTO {
    private long id;
    private Date created;
    private int occupancy;
    private int capacity;

    public GameDTO(Game game){
        this.id = game.getId();
        this.created = game.getCreated();
        this.occupancy = game.numberOfPlayers();
        this.capacity = game.getCapacity();
    }

    public long getId() { return id; }
    public Date getCreated() { return created; }
    public int getOccupancy() { return occupancy; }
    public int getCapacity() { return capacity; }
}
