package games.CutBomb.dto;

import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class GameViewDTO {
    private long id;
//    private String username;
//    private List<String> cards;
    private int numberOfCards;
    private String role;
    private InGamePlayer me;
    private List<InGamePlayer> opponents;

    public GameViewDTO(GamePlay gamePlay){
        this.id = gamePlay.getPlayer().getId();
        this.numberOfCards = gamePlay.getCards().size();
        this.role = gamePlay.getRole();

        this.me = new InGamePlayer(gamePlay, false);
        Game game = gamePlay.getGame();
        this.opponents = game.getGamePlays().stream()
            .filter(gp -> (gp != gamePlay))
            .map(gp -> new InGamePlayer(gp, true))
            .collect(toList());
    }

    public long getId() { return id; }
    public int getNumberOfCards() { return numberOfCards; }
    public String getRole() { return role; }
    public InGamePlayer getMe(){ return me; }
    public List<InGamePlayer> getOpponents() { return opponents; }
}

class InGamePlayer {
    private String username;
    private List<InGameCard> cards;
    private boolean current;
    public InGamePlayer(GamePlay gamePlay, boolean opp){
        this.username = gamePlay.getPlayer().getUsername();
        this.cards = gamePlay.getCards().stream()
                .map(card -> new InGameCard(card.getId(), (opp && card.isHidden()) ? "hidden" : card.getType()))
                .collect(toList());
        this.current = gamePlay.isCurrent();
    }
    public String getUsername() { return username; }
    public List<InGameCard> getCards() { return cards; }
    public boolean isCurrent(){ return current; }
}

class InGameCard{
    private long id;
    private String face;
    public InGameCard(long id, String face) {
        this.id = id;
        this.face = face;
    }
    public long getId() { return id; }
    public String getFace() { return face; }
}