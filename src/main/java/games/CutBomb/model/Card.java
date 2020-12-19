package games.CutBomb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Card {
    //~ Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GameID")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GamePlayID")
    private GamePlay gamePlay;

    //~ Contructor
    public Card(){ }
    public Card(String type, Game game) {
        this.type = type;
        this.game = game; game.getCards().add(this);
    }

    //~ Getters and Setters
    public long getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public GamePlay getGamePlay() { return gamePlay; }
    public void setGamePlay(GamePlay gamePlay) { this.gamePlay = gamePlay; }
}
