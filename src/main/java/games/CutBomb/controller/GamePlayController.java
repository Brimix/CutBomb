package games.CutBomb.controller;

import games.CutBomb.dto.GameViewDTO;
import games.CutBomb.dto.WaitingViewDTO;
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

import java.util.*;
import java.util.stream.Collectors;

import static games.CutBomb.Util.isGuest;
import static games.CutBomb.Util.makeMap;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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

    @RequestMapping(path = "/waitingView/{gamePlayID}", method = RequestMethod.GET)
    public ResponseEntity<Object> waitingView(Authentication auth, @PathVariable Long gamePlayID) {
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        GamePlay gamePlay = gp_rep.findById(gamePlayID).orElse(null);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Invalid GamePlay-ID."), HttpStatus.FORBIDDEN);
        if(!player.getGamePlays().contains(gamePlay))
            return new ResponseEntity<>(makeMap("error", "This isn't your game."), HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(new WaitingViewDTO(gamePlay), HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/GameView/{gamePlayID}", method = RequestMethod.GET)
    public ResponseEntity<Object> gameView(Authentication auth, @PathVariable Long gamePlayID){
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        GamePlay gamePlay = gp_rep.findById(gamePlayID).orElse(null);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Invalid GamePlay-ID."), HttpStatus.FORBIDDEN);
        if(!player.getGamePlays().contains(gamePlay))
            return new ResponseEntity<>(makeMap("error", "This isn't your game."), HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(new GameViewDTO(gamePlay), HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "game/{gamePlayID}/card/{cardID}", method = RequestMethod.POST)
    public ResponseEntity<Object> flipCard(Authentication auth, @PathVariable Long gamePlayID, @PathVariable Long cardID){
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        GamePlay gamePlay = gp_rep.findById(gamePlayID).orElse(null);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Invalid GamePlay-ID."), HttpStatus.FORBIDDEN);
        if(!player.getGamePlays().contains(gamePlay))
            return new ResponseEntity<>(makeMap("error", "This isn't your game."), HttpStatus.UNAUTHORIZED);
        if(!gamePlay.isCurrent())
            return new ResponseEntity<>(makeMap("error", "It's not your turn"), HttpStatus.FORBIDDEN);

        Game game = gamePlay.getGame();
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Game not in database"), HttpStatus.INTERNAL_SERVER_ERROR);
        if(game.isPaused())
            return new ResponseEntity<>(makeMap("error", "Round has finished"), HttpStatus.FORBIDDEN);

        Card card = card_rep.findById(cardID).orElse(null);
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

        Map<String, Object> ret = makeMap("OK", "You flipped a card!");

        Set<String> deck = game.getDeck().stream().map(cd -> cd.getType()).collect(toSet());
        if(!deck.contains("bomb")){ game.setState("Bomb exploded!"); endGame(game); }
        else if(!deck.contains("wire")){ game.setState("Bomb defused!"); endGame(game); }
        else if(game.getDeck().size() == game.getGamePlays().size()){ game.setState("Time out!"); endGame(game); }
        else if(game.getDeck().size()%game.getGamePlays().size() == 0){
            game.setPaused(true);
            game.setState("Round over! Dealing cards again...");
            ret.replace("OK", "Deal again");
        }
        else game.setState(gamePlay.getPlayer().getUsername() + " flipped a " + card.getType());

        game_rep.save(game); card_rep.save(card);
        gp_rep.save(gamePlay); gp_rep.save(target);
        return new ResponseEntity<>(ret, HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "game/{gamePlayID}/deal", method = RequestMethod.POST)
    public ResponseEntity<Object> nextRound(Authentication auth, @PathVariable Long gamePlayID){
        GamePlay gamePlay = gp_rep.findById(gamePlayID).orElse(null);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Invalid GamePlay-ID."), HttpStatus.FORBIDDEN);

        Game game = gamePlay.getGame();
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Game not in database"), HttpStatus.INTERNAL_SERVER_ERROR);

        for(GamePlay gp : game.getGamePlays()) gp.returnCards();
        for(Card card : game.getDeck()) card.setHidden(true);
        dealCards(game);
        game.setPaused(false);
        game.setState("New round started!");
        game_rep.save(game); gp_rep.saveAll(game.getGamePlays());
        return new ResponseEntity<>(makeMap("OK", "Cards are dealt."), HttpStatus.ACCEPTED);
    }
    void dealCards(Game game){
        int n = game.numberOfPlayers();
        List<GamePlay> player = game.getGamePlays().stream().collect(toList());
        List<Card> deck = game.getDeck().stream().collect(toList());

        shuffle(deck);
        for(int i = 0; i < deck.size(); i++) player.get(i%n).addCard(deck.get(i));

        gp_rep.saveAll(player);
    }
    void endGame(Game game){
        game.setPaused(true);
        game.setFinished(new Date());
        game = game_rep.save(game);
    }
}
