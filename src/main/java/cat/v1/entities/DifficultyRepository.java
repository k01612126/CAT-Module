package cat.v1.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DifficultyRepository extends CrudRepository<Difficulty, Integer> {
    Optional<Difficulty> findById(DifficultyCompositeKey id);

    Boolean existsById(DifficultyCompositeKey id);

    @Transactional
    void deleteById(DifficultyCompositeKey id);
}