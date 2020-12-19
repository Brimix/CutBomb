package games.CutBomb.repository;

import games.CutBomb.model.GamePlay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GamePlayRepository extends JpaRepository<GamePlay, Long> {
}
