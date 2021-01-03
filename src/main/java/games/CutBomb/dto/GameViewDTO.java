package games.CutBomb.dto;

import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class GameViewDTO {
    private long id;
    private int numberOfCards;
    private long wiresLeft;
    private String role;
    private InGamePlayer me;
    private List<InGamePlayer> opponents;
    private String state;
    private long blanks;
    private long wires;
    private long bombs;

    public GameViewDTO(GamePlay gamePlay){
        this.id = gamePlay.getId();
        this.numberOfCards = gamePlay.getCards().size();
        this.role = gamePlay.getRole();
        this.blanks = gamePlay.getCards().stream().filter(card -> (card.getType() == "blank")).count();
        this.wires = gamePlay.getCards().stream().filter(card -> (card.getType() == "wire")).count();
        this.bombs = gamePlay.getCards().stream().filter(card -> (card.getType() == "bomb")).count();

        this.me = new InGamePlayer(gamePlay, true);
        Game game = gamePlay.getGame();
        this.opponents = game.getGamePlays().stream()
            .sorted(Comparator.comparing(GamePlay::getJoined))
            .filter(gp -> (gp != gamePlay))
            .map(gp -> new InGamePlayer(gp, true))
            .collect(toList());
        this.wiresLeft = game.getGamePlays().stream()
            .mapToLong(gp -> gp.getCards().stream().filter(card -> (card.getType() == "wire")).count())
            .sum();
        this.state = game.getState();
    }

    public long getId() { return id; }
    public int getNumberOfCards() { return numberOfCards; }
    public long getWiresLeft() { return wiresLeft; }
    public String getRole() { return role; }
    public String getState() { return state; }
    public long getBlanks() { return blanks; }
    public long getWires() { return wires; }
    public long getBombs() { return bombs; }
    public InGamePlayer getMe(){ return me; }
    public List<InGamePlayer> getOpponents() { return opponents; }
}

class InGamePlayer {
    private Long id;
    private String username;
    private List<InGameCard> cards;
    private boolean current;
    public InGamePlayer(GamePlay gamePlay, boolean opp){
        this.id = gamePlay.getId();
        this.username = gamePlay.getPlayer().getUsername();
        this.cards = gamePlay.getCards().stream()
                .map(card -> new InGameCard(card.getId(), (opp && card.isHidden()) ? "hidden" : card.getType()))
                .collect(toList());
        this.current = gamePlay.isCurrent();
    }
    public Long getId(){ return id; }
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