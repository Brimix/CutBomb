package games.CutBomb.controller;

import games.CutBomb.dto.GameDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static games.CutBomb.Util.isGuest;
import static games.CutBomb.Util.makeMap;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    PlayerRepository player_rep;
    @Autowired
    GameRepository game_rep;
    @Autowired
    GamePlayRepository gp_rep;
    @Autowired
    CardRepository card_rep;

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public List<GameDTO> GamesList(Authentication auth){
        Player player = player_rep.findByUsername(auth.getName()).get(); // CHEQUEAR

        return game_rep.findAll().stream().map(game -> {
            GameDTO ret = new GameDTO(game);
            ret.setAlreadyIn(game.getGamePlays().stream().map(gp -> gp.getPlayer()).anyMatch(p -> (p == player)));
            return ret;
        }).collect(toList());
    }
    @RequestMapping(path = "/game/{gameID}/gamePlay", method = RequestMethod.GET)
    public ResponseEntity<Object> getGamePlayID(Authentication auth, @PathVariable Long gameID) {
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        Game game = game_rep.findById(gameID).orElse(null);
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Invalid Game-ID."), HttpStatus.FORBIDDEN);
        if(game.getGamePlays().stream().noneMatch(gp -> (player == gp.getPlayer())))
            return new ResponseEntity<>(makeMap("error", "You are not in the game!"), HttpStatus.FORBIDDEN);

        GamePlay gamePlay = game.getGamePlays().stream().filter(gp -> (player == gp.getPlayer())).findAny().orElse(null);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Error getting gamePlay"), HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(makeMap("gpid", gamePlay.getId()), HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/CreateGame", method = RequestMethod.POST)
    public ResponseEntity<Object> create(Authentication auth, @RequestParam int capacity) {
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        if(capacity < 3 || 8 < capacity)
            return new ResponseEntity<>(makeMap("error", "Input capacity is invalid."), HttpStatus.FORBIDDEN);

        Game game = new Game(capacity);
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Game couldn't be created."), HttpStatus.INTERNAL_SERVER_ERROR);

        GamePlay gamePlay = new GamePlay(player, game);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Gameplay couldn't be created."), HttpStatus.INTERNAL_SERVER_ERROR);

        game = game_rep.save(game);
        gamePlay = gp_rep.save(gamePlay);
        game.setHost(gamePlay); game_rep.save(game);

        return new ResponseEntity<>(makeMap("gpid", gamePlay.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/JoinGame/{gameID}", method = RequestMethod.POST)
    public ResponseEntity<Object> join(Authentication auth, @PathVariable Long gameID) {
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);
        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        Game game = game_rep.findById(gameID).orElse(null);
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Invalid Game-ID."), HttpStatus.FORBIDDEN);
        if(game.getGamePlays().stream().anyMatch(gp -> (player == gp.getPlayer())))
            return new ResponseEntity<>(makeMap("error", "You are already in the game!"), HttpStatus.FORBIDDEN);
        if(game.numberOfPlayers() == game.getCapacity())
            return new ResponseEntity<>(makeMap("error", "Game is full!"), HttpStatus.FORBIDDEN);

        GamePlay gamePlay = new GamePlay(player, game);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Gameplay couldn't be created."), HttpStatus.INTERNAL_SERVER_ERROR);

        gp_rep.save(gamePlay);
        return new ResponseEntity<>(makeMap("gpid", gamePlay.getId()), HttpStatus.CREATED);
    }
    @RequestMapping(path = "/StartGame/{gamePlayID}", method = RequestMethod.POST)
    public ResponseEntity<Object> start(Authentication auth, @PathVariable Long gamePlayID) {
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

        Game game = gamePlay.getGame();
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Game couldn't be found."), HttpStatus.INTERNAL_SERVER_ERROR);

        if(gamePlay != game.getHost())
            return new ResponseEntity<>(makeMap("error", "You're not the host."), HttpStatus.FORBIDDEN);

        game.setStarted(new Date()); game = game_rep.save(game);
        formatGame(game); game = game_rep.save(game);
        return new ResponseEntity<>(makeMap("OK", "Game is ready!"), HttpStatus.ACCEPTED);
    }

    void formatGame(Game game){
        int n = game.numberOfPlayers();
        int b = (n < 5 ? 1 : (n < 8 ? 2 : 3)); // Maybe can be done with a formula

        List<GamePlay> player = game.getGamePlays().stream().collect(toList());
        shuffle(player);
        for(int i = 0; i < n; i++) player.get(i).setRole(i < b ? "Criminal" : "Savior");

        List<Card> deck = new ArrayList<>();
        deck.add(new Card("bomb", game));
        for(int i = 0; i < n; i++) deck.add(new Card("wire", game));
        for(int i = 0; i < 4*n-1; i++) deck.add(new Card("blank", game));
        card_rep.saveAll(deck);
        dealCards(game);

        Random rnd = new Random();
        int starter_id = rnd.nextInt(n);
        player.get(starter_id).setCurrent(true);
        game.setState("Game has just started!");
    }

    void dealCards(Game game){
        int n = game.numberOfPlayers();
        List<GamePlay> player = game.getGamePlays().stream().collect(toList());
        List<Card> deck = game.getDeck().stream().collect(toList());

        shuffle(deck);
        for(int i = 0; i < deck.size(); i++) player.get(i%n).addCard(deck.get(i));

        gp_rep.saveAll(player);
    }
}
