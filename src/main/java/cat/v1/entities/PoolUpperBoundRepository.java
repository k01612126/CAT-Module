package cat.v1.entities;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PoolUpperBoundRepository extends CrudRepository<PoolUpperBound, Integer> {

    List<PoolUpperBound> findByTopicId(Integer topic);

    Boolean existsByTopicId(Integer topic);

    @Query(value = "SELECT * FROM pool_upper_bound WHERE ABS(upper_bound-?2) < 0.00001 AND topic_id=?1", nativeQuery = true)
    Optional<PoolUpperBound> findByTopicIdAndUpperBound(Integer topicId, Float upperBound);
}