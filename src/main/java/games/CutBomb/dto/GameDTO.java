package games.CutBomb.dto;

import games.CutBomb.model.Game;

import java.util.Date;

public class GameDTO {
    private long id;
    private Date created;

    public GameDTO(Game game){
        this.id = game.getId();
        this.created = game.getCreated();
    }

    public long getId() { return id; }
    public Date getCreated() { return created; }
}
