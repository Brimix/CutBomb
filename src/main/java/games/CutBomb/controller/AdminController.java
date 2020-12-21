package games.CutBomb.controller;

import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;
import games.CutBomb.repository.CardRepository;
import games.CutBomb.repository.GamePlayRepository;
import games.CutBomb.repository.GameRepository;
import games.CutBomb.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    PlayerRepository player_rep;
    @Autowired
    GameRepository game_rep;
    @Autowired
    GamePlayRepository gp_rep;
    @Autowired
    CardRepository card_rep;

    @RequestMapping(path = "/players", method = RequestMethod.GET)
    public List<Map<String, Object>> getPlayers(){
        return player_rep.findAll().stream()
                .map( player -> {
                    Map<String, Object> dto = new LinkedHashMap<>();
                    List<Long> games = new ArrayList<>();
                    dto.put("id", player.getId());
                    dto.put("username", player.getUsername());
                    dto.put("games", games);
                    for(GamePlay gp : player.getGamePlays()) games.add(gp.getGame().getId());
                    return dto;
                }).collect(toList());
    }

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public List<Map<String, Object>> getGames(){
        return game_rep.findAll().stream()
                .map( game -> {
                    Map<String, Object> dto = new LinkedHashMap<>();
                    List<String> players = new ArrayList<>();
                    dto.put("id", game.getId());
                    dto.put("players", game.getGamePlays().stream().map( gp -> {
                                Map<String, Object> dtoPlayer = new LinkedHashMap<>();
                                dtoPlayer.put("gpid", gp.getId());
                                dtoPlayer.put("player_id", gp.getPlayer().getId());
                                dtoPlayer.put("player", gp.getPlayer().getUsername());
                                return dtoPlayer;
                            }).collect(toList()));
                    dto.put("cards", game.getCards().stream().map(card -> card.getType()).collect(toList()));
                    dto.put("host", game.getHost().getId());
//                    dto.put("current", game.getHost().getId());
                    return dto;
                }).collect(toList());
    }

    @RequestMapping(path = "/gamePlays", method = RequestMethod.GET)
    public List<Map<String, Object>> getGamePlays(){
        return gp_rep.findAll().stream()
                .map( gp -> {
                    Map<String, Object> dto = new LinkedHashMap<>();
                    dto.put("id", gp.getId());
                    dto.put("player_id", gp.getPlayer().getId());
                    dto.put("player", gp.getPlayer().getUsername());
                    dto.put("game_id", gp.getGame().getId());
                    dto.put("cards", gp.getCards().stream().map(card -> card.getType()).collect(toList()));
                    return dto;
                }).collect(toList());
    }
}
