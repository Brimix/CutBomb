package games.CutBomb.controller;

import games.CutBomb.dto.GameViewDTO;
import games.CutBomb.model.Card;
import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;
import games.CutBomb.model.Player;
import games.CutBomb.repository.CardRepository;
import games.CutBomb.repository.GamePlayRepository;
import games.CutBomb.repository.GameRepository;
import games.CutBomb.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static games.CutBomb.Util.isGuest;
import static games.CutBomb.Util.makeMap;

@RestController
@RequestMapping("/api")
public class GamePlayController {
    @Autowired
    PlayerRepository player_rep;
    @Autowired
    GameRepository game_rep;
    @Autowired
    GamePlayRepository gp_rep;
    @Autowired
    CardRepository card_rep;

    @RequestMapping(path = "/GameView/{id}", method = RequestMethod.GET)
    public GameViewDTO GameView(@PathVariable Long id){
        GamePlay gamePlay = gp_rep.findById(id).orElse(null);
        if(gamePlay == null)
            return null;
        return new GameViewDTO(gamePlay);
    }

    @RequestMapping(path = "game/{gpid}/card/{id}", method = RequestMethod.POST)
    public ResponseEntity<Object> FlipCard(Authentication auth, @PathVariable Long gpid, @PathVariable Long id){
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        GamePlay gamePlay = gp_rep.findById(gpid).orElse(null);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Invalid GamePlay-ID."), HttpStatus.FORBIDDEN);
        if(!player.getGamePlays().contains(gamePlay))
            return new ResponseEntity<>(makeMap("error", "This isn't your game."), HttpStatus.UNAUTHORIZED);
        if(!gamePlay.isCurrent())
            return new ResponseEntity<>(makeMap("error", "It's not your turn"), HttpStatus.FORBIDDEN);

        Game game = gamePlay.getGame();
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Game not in database"), HttpStatus.INTERNAL_SERVER_ERROR);

        Card card = card_rep.findById(id).orElse(null);
        if(card == null)
            return new ResponseEntity<>(makeMap("error", "Invalid Card-ID"), HttpStatus.FORBIDDEN);
        if(!game.getDeck().contains(card))
            return new ResponseEntity<>(makeMap("error", "The chosen card is not in the game."), HttpStatus.FORBIDDEN);
        if(gamePlay.getCards().contains(card))
            return new ResponseEntity<>(makeMap("error", "You can't choose your own card."), HttpStatus.FORBIDDEN);

        GamePlay target = card.getGamePlay();
        if(target == null)
            return new ResponseEntity<>(makeMap("error", "Invalid Target GamePlay-ID."), HttpStatus.INTERNAL_SERVER_ERROR);

        game.discard(card);
        card.setHidden(false);
        gamePlay.setCurrent(false);
        target.setCurrent(true);

        game_rep.save(game); card_rep.save(card);
        gp_rep.save(gamePlay); gp_rep.save(target);
        return new ResponseEntity<>("You flipped a card!", HttpStatus.ACCEPTED);
    }
}
