package cat.v1.entities;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {

    @Query(value = "SELECT DISTINCT q.* FROM question q, difficulty d WHERE q.id=d.question_id AND d.topic_id=?1", nativeQuery = true)
    Iterable<Question> findDistinctByTopicId(Integer topicId);
}
