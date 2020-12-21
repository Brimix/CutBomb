package games.CutBomb.model;

import net.minidev.json.annotate.JsonIgnore;
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

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Card> cards;

    //~ Constructors
    public Game() {
        this.created = new Date();
        this.gamePlays = new HashSet<>();
        this.cards = new HashSet<>();
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
    public Set<Card> getCards() { return cards; }
    public void setCards(Set<Card> cards) { this.cards = cards; }

    //~ Methods
    public int numberOfPlayers(){ return this.gamePlays.size(); }
}
