package cat.v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Question {
    @JsonManagedReference(value = "question-difficulty")
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<Difficulty> difficulties;
    @Id
    private Long id;
    private LocalDateTime lastModified;
    @NotNull
    private boolean isActive;

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public Set<Difficulty> getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(Set<Difficulty> difficulties) {
        this.difficulties = difficulties;
    }

    public float getDifficulty(int topicId) {
        for (Difficulty d : this.difficulties) {
            if (d.getId().getTopicId() == topicId) return d.getDifficulty();
        }
        return -99;
    }
}

