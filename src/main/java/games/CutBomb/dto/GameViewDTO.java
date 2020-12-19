package games.CutBomb.dto;

import games.CutBomb.model.GamePlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameViewDTO {
    private long id;
    private String username;
    private List<String> cards;
    private List<Opponent> opponent;

    public GameViewDTO(GamePlay gamePlay){
        this.id = gamePlay.getPlayer().getId();
        this.username = gamePlay.getPlayer().getUsername();
        this.cards = gamePlay.getCards().stream().map(card -> card.getType()).collect(Collectors.toList());

    }
}

class Opponent{
    private String username;
    List<String> cards;
    public Opponent(GamePlay gamePlay){
        this.username = gamePlay.getPlayer().getUsername();
        this.cards = new ArrayList<>();
        this.cards = gamePlay.getCards().stream().map(card ->{
            if(card.getHidden()) return "hidden";
            return card.getType();
        })
    }
}