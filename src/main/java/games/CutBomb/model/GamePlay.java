package games.CutBomb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class GamePlay {
    //~ Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String role;
    private boolean current;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PlayerID")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GameID")
    private Game game;

    @OneToMany(mappedBy = "gamePlay", fetch = FetchType.EAGER)
    private Set<Card> cards;

    //~ Constructors
    public GamePlay(){
        this.cards = new HashSet<>();
    }
    public GamePlay(String role, Player player, Game game){
        this();
        this.role = role;
        this.player = player; player.getGamePlays().add(this);
        this.game = game; game.getGamePlays().add(this);
        this.current = false;
    }

    //~ Getters and Setters
    public long getId() { return id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Set<Card> getCards() { return cards; }
    public void setCards(Set<Card> cards) { this.cards = cards; }
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }

    //~ Methods
    public void addCard(Card card){
        this.cards.add(card);
        card.setGamePlay(this);
    }
}
