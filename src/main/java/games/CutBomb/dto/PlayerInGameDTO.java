package games.CutBomb.dto;

import games.CutBomb.model.GamePlay;

import java.util.Date;

public class PlayerInGameDTO {
    private long id;
    private String username;
    private Date joined;

    public PlayerInGameDTO(GamePlay gamePlay){
        this.id = gamePlay.getPlayer().getId();
        this.username = gamePlay.getPlayer().getUsername();
        this.joined = gamePlay.getJoined();
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public Date getJoined() { return joined; }
}
