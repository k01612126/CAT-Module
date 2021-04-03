package cat.v1.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Topic {
    @JsonManagedReference(value = "topic-difficulty")
    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<Difficulty> difficulties;

    @JsonManagedReference(value = "topic-poolUpperBound")
    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<PoolUpperBound> poolUpperBounds;

    @Id
    private Integer id;

    @NotNull
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Difficulty> getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(Set<Difficulty> difficulties) {
        this.difficulties = difficulties;
    }

    public Set<PoolUpperBound> getPoolUpperBounds() {
        return poolUpperBounds;
    }

    public void setPoolUpperBounds(Set<PoolUpperBound> poolUpperBounds) {
        this.poolUpperBounds = poolUpperBounds;
    }

    public void addPoolUpperBounds(Set<PoolUpperBound> poolUpperBounds) {
        this.poolUpperBounds.addAll(poolUpperBounds);
    }

    public void addPoolUpperBound(PoolUpperBound poolUpperBounds) {
        this.poolUpperBounds.add(poolUpperBounds);
    }
}
