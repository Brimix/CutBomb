package games.CutBomb.dto;

import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class WaitingViewDTO {
    private List<PlayerInGame> players;
    private boolean host;
    private boolean started;
    private int count;

    public WaitingViewDTO(GamePlay gamePlay){
        this.players = gamePlay.getGame().getGamePlays().stream()
                .sorted(Comparator.comparing(GamePlay::getJoined))
                .map(gp -> new PlayerInGame(gp))
                .collect(toList());
        this.host = gamePlay.isHost();
        this.started = (gamePlay.getGame().getStarted() != null);
        this.count = players.size();
    }

    public List<PlayerInGame> getPlayers() { return players; }
    public boolean isHost() { return host; }
    public boolean isStarted() { return started; }
    public int getCount() { return count; }
}

class PlayerInGame{
    private long id;
    private String username;
    private Date joined;
    private boolean host;

    public PlayerInGame(GamePlay gamePlay){
        this.id = gamePlay.getPlayer().getId();
        this.username = gamePlay.getPlayer().getUsername();
        this.joined = gamePlay.getJoined();
        this.host = gamePlay.isHost();
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public Date getJoined() { return joined; }
    public boolean getHost() { return host; }
}