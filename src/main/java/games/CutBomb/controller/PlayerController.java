package games.CutBomb.controller;

import games.CutBomb.model.Player;
import games.CutBomb.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static games.CutBomb.Util.isGuest;
import static games.CutBomb.Util.makeMap;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    PlayerRepository player_rep;
    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String user,
            @RequestParam String pass,
            Authentication auth){
//        System.out.println("Entramosss");
//        if(!isGuest(auth))
//            return new ResponseEntity<>(makeMap("error", "Logout to register as a new player."), HttpStatus.UNAUTHORIZED);

        if(player_rep.findByUsername(user).isPresent())
            return new ResponseEntity<>(makeMap("error", "Username already taken."), HttpStatus.UNAUTHORIZED);

        if(user.isEmpty() || pass.isEmpty())
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);

        player_rep.save(new Player(user, passwordEncoder.encode(pass)));

        return new ResponseEntity<>(makeMap("OK", "Registered"), HttpStatus.CREATED);
    }
}
