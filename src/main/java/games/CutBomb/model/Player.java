package games.CutBomb.model;

import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Player {
    //~ Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String username;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlay> gamePlays;

    //~ Constructors
    public Player(){
        this.gamePlays = new HashSet<>();
    }
    public Player(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    //~ Getters and Setters
    public long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<GamePlay> getGamePlays() { return gamePlays; }
    public void setGamePlays(Set<GamePlay> gamePlays) { this.gamePlays = gamePlays; }
}
