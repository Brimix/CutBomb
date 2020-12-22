package games.CutBomb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Game {
    //~ Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date created;
    private Date started;
    private int capacity;

    @OneToOne(fetch=FetchType.EAGER)
    private GamePlay host;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlay> gamePlays;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Card> deck;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Card> discarded;

    //~ Constructors
    public Game() {
        this.created = new Date();
        this.started = null;
        this.gamePlays = new HashSet<>();
        this.deck = new HashSet<>();
        this.discarded = new HashSet<>();
    }
    public Game(int capacity){
        this();
        this.capacity = capacity;
    }

    //~ Getters and Setters
    public long getId() { return id; }
    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }
    public Date getStarted() { return started; }
    public void setStarted(Date started) { this.started = started; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public GamePlay getHost() { return host; }
    public void setHost(GamePlay host) { this.host = host; }
    public Set<GamePlay> getGamePlays() { return gamePlays; }
    public void setGamePlays(Set<GamePlay> gamePlays) { this.gamePlays = gamePlays; }
    public Set<Card> getDeck() { return deck; }
    public void setDeck(Set<Card> deck) { this.deck = deck; }
    public Set<Card> getDiscarded() { return discarded; }
    public void setDiscarded(Set<Card> discarded) { this.discarded = discarded; }

    //~ Methods
    public int numberOfPlayers(){ return this.gamePlays.size(); }
    public boolean discard(Card card){
        if(!deck.contains(card)) return false;
        deck.remove(card); discarded.add(card);
        return true;
    }
}
