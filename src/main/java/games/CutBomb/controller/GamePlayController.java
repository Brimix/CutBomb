package games.CutBomb.controller;

import games.CutBomb.dto.GameViewDTO;
import games.CutBomb.model.GamePlay;
import games.CutBomb.repository.GamePlayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GamePlayController {
    @Autowired
    GamePlayRepository gp_rep;

    @RequestMapping(path = "/GameView/{id}", method = RequestMethod.GET)
    public GameViewDTO GameView(@PathVariable Long id){
        GamePlay gamePlay = gp_rep.findById(id).orElse(null);
        if(gamePlay == null)
            return null;
        return new GameViewDTO(gamePlay);
    }
}
