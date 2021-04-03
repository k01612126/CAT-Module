package cat.v1.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DifficultyCompositeKey implements Serializable {
    Integer topicId;
    Long questionId;

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DifficultyCompositeKey that = (DifficultyCompositeKey) o;
        return topicId.equals(that.topicId) && questionId.equals(that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId, questionId);
    }
}
