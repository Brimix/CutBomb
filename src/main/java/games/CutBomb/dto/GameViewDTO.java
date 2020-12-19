package games.CutBomb.dto;

import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class GameViewDTO {
    private long id;
    private String username;
    private List<String> cards;
    private List<Opponent> opponent;

    public GameViewDTO(GamePlay gamePlay){
        this.id = gamePlay.getPlayer().getId();
        this.username = gamePlay.getPlayer().getUsername();
        this.cards = gamePlay.getCards().stream().map(card -> card.getType()).collect(toList());

        Game game = gamePlay.getGame();
        this.opponent = game.getGamePlays().stream()
                    .filter(gp -> (gp != gamePlay))
                    .map(gp -> new Opponent(gp))
                    .collect(toList());
    }
}

class Opponent{
    private String username;
    List<String> cards;
    public Opponent(GamePlay gamePlay){
        this.username = gamePlay.getPlayer().getUsername();
        this.cards = new ArrayList<>();
        this.cards = gamePlay.getCards().stream().map(card ->{
            if(card.isHidden()) return "hidden";
            return card.getType();
        }).collect(toList());
    }
}