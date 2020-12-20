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
    private int playersNumber;
    private int capacity;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlay> gamePlays;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Card> cards;

    //~ Constructors
    public Game() {
        this.created = new Date();
        this.playersNumber = 0;
        this.gamePlays = new HashSet<>();
        this.cards = new HashSet<>();
    }
    public Game(int capacity){
        this();
        this.capacity = capacity;
    }

    //~ Getters and Setters
    public long getId() { return id; }
    public Set<GamePlay> getGamePlays() { return gamePlays; }
    public void setGamePlays(Set<GamePlay> gamePlays) { this.gamePlays = gamePlays; }
    public Set<Card> getCards() { return cards; }
    public void setCards(Set<Card> cards) { this.cards = cards; }
}
