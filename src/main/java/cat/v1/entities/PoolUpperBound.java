package cat.v1.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity()
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"topicId", "upperBound"})})
public class PoolUpperBound {

    @JsonBackReference(value = "topic-poolUpperBound")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId")
    private Topic topic;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;

    @NotNull
    @JoinColumn(name = "upperBound")
    private float upperBound;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }
}
