package cat.v1.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
public class Difficulty {
    @EmbeddedId
    private DifficultyCompositeKey id;

    @ManyToOne
    @JsonBackReference(value = "question-difficulty")
    @MapsId("questionId")
    @JoinColumn(name = "questionId")
    private Question question;

    @ManyToOne
    @JsonBackReference(value = "topic-difficulty")
    @MapsId("topicId")
    @JoinColumn(name = "topicId")
    private Topic topic;

    @NotNull
    private float difficulty;

    public DifficultyCompositeKey getId() {
        return id;
    }

    public void setId(DifficultyCompositeKey id) {
        this.id = id;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
