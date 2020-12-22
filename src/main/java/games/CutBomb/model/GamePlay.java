package games.CutBomb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlay {
    //~ Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String role;
    private boolean current;
    private Date joined;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PlayerID")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GameID")
    private Game game;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Card> cards;

    //~ Constructors
    public GamePlay(){
        this.cards = new ArrayList<>();
        this.current = false;
        this.joined = new Date();
    }
    public GamePlay(Player player, Game game){
        this();
        this.player = player; player.getGamePlays().add(this);
        this.game = game; game.getGamePlays().add(this);
    }
    // This one is only for testing. Must be removed later
    public GamePlay(String role, Player player, Game game){
        this(player, game);
        this.role = role;
    }

    //~ Getters and Setters
    public long getId() { return id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
    public Date getJoined() { return joined; }
    public void setJoined(Date joined) { this.joined = joined; }

    //~ Methods
    public void addCard(Card card){
        this.cards.add(card);
        card.setGamePlay(this);
    }
    public boolean isHost(){
        return (this == game.getHost());
    }
}
