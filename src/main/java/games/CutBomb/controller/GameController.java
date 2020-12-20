package games.CutBomb.controller;

import games.CutBomb.dto.GameDTO;
import games.CutBomb.dto.GameViewDTO;
import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;
import games.CutBomb.model.Player;
import games.CutBomb.repository.GamePlayRepository;
import games.CutBomb.repository.GameRepository;
import games.CutBomb.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static games.CutBomb.Util.isGuest;
import static games.CutBomb.Util.makeMap;
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

    @RequestMapping(path = "/GameView/{id}", method = RequestMethod.GET)
    public GameViewDTO GameView(@PathVariable Long id){
        GamePlay gamePlay = gp_rep.findById(id).orElse(null);
        if(gamePlay == null)
            return null;
        return new GameViewDTO(gamePlay);
    }

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public List<GameDTO> GamesList(){
        return game_rep.findAll().stream().map(game -> new GameDTO(game)).collect(toList());
    }

    @RequestMapping(path = "/CreateGame", method = RequestMethod.POST)
    public ResponseEntity<Object> create(Authentication auth, @RequestParam int capacity) {
        if(isGuest(auth))
            return new ResponseEntity<>(makeMap("error", "You're not logged in."), HttpStatus.UNAUTHORIZED);

        Player player = player_rep.findByUsername(auth.getName()).orElse(null);
        if(player == null)
            return new ResponseEntity<>(makeMap("error", "Player not in database."), HttpStatus.INTERNAL_SERVER_ERROR);

        if(capacity < 1 || 8 < capacity)
            return new ResponseEntity<>(makeMap("error", "Input capacity is invalid."), HttpStatus.FORBIDDEN);

        Game game = new Game(capacity);
        if(game == null)
            return new ResponseEntity<>(makeMap("error", "Game couldn't be created."), HttpStatus.INTERNAL_SERVER_ERROR);

        GamePlay gamePlay = new GamePlay(player, game);
        if(gamePlay == null)
            return new ResponseEntity<>(makeMap("error", "Gameplay couldn't be created."), HttpStatus.INTERNAL_SERVER_ERROR);

        game_rep.save(game);
        gp_rep.save(gamePlay);
        return new ResponseEntity<>("Game created!", HttpStatus.CREATED); // WARNING the body!!!!!!!!!!
    }
}
