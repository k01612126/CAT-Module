package cat.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "/catV1")
public class MaintenanceController {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private DifficultyRepository difficultyRepository;
    @Autowired
    private PoolUpperBoundRepository poolUpperBoundRepository;

    @PostMapping(path = "/topic")
    public @ResponseBody
    ResponseEntity<Topic> addNewTopic(@RequestBody TopicPojo topicPojo) {
        if (topicRepository.existsById(topicPojo.getId())) return new ResponseEntity<>(HttpStatus.CONFLICT);
        try {
            Topic topic = new Topic();
            topic.setId(topicPojo.getId());
            topic.setName(topicPojo.getName());
            return new ResponseEntity<>(topicRepository.save(topic), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/topics")
    public @ResponseBody
    ResponseEntity<Iterable<Topic>> getAllTopics() {
        try {
            return new ResponseEntity<>(topicRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/topic/{id}")
    public @ResponseBody
    ResponseEntity<Object> getTopic(@PathVariable Integer id) {
        if (!topicRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            return new ResponseEntity<>(topicRepository.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/topic/{id}")
    public @ResponseBody
    ResponseEntity<Topic> updateTopic(@PathVariable Integer id, @RequestBody TopicPojo topicPojo) {
        Optional<Topic> topicData = topicRepository.findById(id);
        if (topicData.isPresent()) {
            Topic topic = topicData.get();
            topic.setName(topicPojo.getName());
            return new ResponseEntity<>(topicRepository.save(topic), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/topic/{id}")
    public @ResponseBody
    ResponseEntity<String> deleteTopic(@PathVariable Integer id) {
        try {
            topicRepository.deleteById(id);
            return new ResponseEntity<>("Topic " + id + " was successfuly delted.", HttpStatus.OK);
        } catch (Exception e) {
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @PostMapping(path = "/topic/{id}/poolUpperBound")
    public @ResponseBody
    ResponseEntity<Topic> addNewPoolUpperBound(@PathVariable Integer id, @RequestBody PoolUpperBoundPojo poolUpperBoundPojo) {
        Optional<Topic> topicData = topicRepository.findById(poolUpperBoundPojo.getTopicId());
        if (topicData.isPresent()) {
            Topic topic = topicData.get();
            PoolUpperBound pUB = new PoolUpperBound();
            pUB.setTopic(topic);
            pUB.setUpperBound(poolUpperBoundPojo.getUpperBound());
            topic.addPoolUpperBound(pUB);
            try {
                return new ResponseEntity<>(topicRepository.save(topic), HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/topic/{id}/poolUpperBounds")
    public @ResponseBody
    ResponseEntity<Iterable<PoolUpperBound>> getAllPoolUpperBounds(@PathVariable Integer id) {
        try {
            if (!poolUpperBoundRepository.existsByTopicId(id)) return new ResponseEntity<>((HttpStatus.NOT_FOUND));
            return new ResponseEntity<>(poolUpperBoundRepository.findByTopicId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/topic/{id}/poolUpperBound")
    public @ResponseBody
    ResponseEntity<PoolUpperBound> updatePoolUpperBound(@PathVariable Integer id, @RequestBody ChangeUpperBoundPojo changeUpperBoundPojo) {
        Optional<PoolUpperBound> poolUpperBoundData = poolUpperBoundRepository.findByTopicIdAndUpperBound(id, changeUpperBoundPojo.getOldUpperBound());
        if (poolUpperBoundData.isPresent()) {
            PoolUpperBound poolUpperBound = poolUpperBoundData.get();
            poolUpperBound.setUpperBound(changeUpperBoundPojo.getNewUpperBound());
            try {
                return new ResponseEntity<>(poolUpperBoundRepository.save(poolUpperBound), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/topic/{id}/poolUpperBound")
    public @ResponseBody
    ResponseEntity<String> deletePoolUpperBound(@PathVariable Integer id, @RequestBody PoolUpperBound poolUpperBound) {
        try {
            int uBId = poolUpperBoundRepository.findByTopicIdAndUpperBound(id, poolUpperBound.getUpperBound()).get().getId();
            poolUpperBoundRepository.deleteById(uBId);
            return new ResponseEntity<>("PoolUpperBound " + uBId + " with UpperBound " + poolUpperBound.getUpperBound() + " in Topic " + id + " successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/question")
    public @ResponseBody
    ResponseEntity<Question> addNewQuestion(@RequestBody Question question) {
        try {
            if (questionRepository.existsById(question.getId())) return new ResponseEntity<>(HttpStatus.CONFLICT);
            return new ResponseEntity<>(questionRepository.save(question), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/questions")
    public @ResponseBody
    ResponseEntity<Iterable<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/topic/{id}/questions")
    public @ResponseBody
    ResponseEntity<Iterable<Question>> getQuestionsByTopic(@PathVariable Integer id) {
        if (!topicRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            return new ResponseEntity<>(questionRepository.findDistinctByTopicId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/question/{id}")
    public @ResponseBody
    ResponseEntity<Object> getQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            return new ResponseEntity<>(questionRepository.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/question/{id}")
    public @ResponseBody
    ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question requestQuestion) {
        Optional<Question> questionData = questionRepository.findById(id);
        if (questionData.isPresent()) {
            Question question = questionData.get();
            question.setActive(requestQuestion.isActive());
            question.setLastModified(requestQuestion.getLastModified());
            try {
                return new ResponseEntity<>(questionRepository.save(question), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/question/{id}")
    public @ResponseBody
    ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        try {
            questionRepository.deleteById(id);
            return new ResponseEntity<>("Question " + id + " successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return (new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @PostMapping(path = "/difficulty")
    public @ResponseBody
    ResponseEntity<Difficulty> addNewDifficulty(@RequestBody DifficultyPojo difficultyPojo) {
        //Create Composite Key
        DifficultyCompositeKey dCK = new DifficultyCompositeKey();
        dCK.setTopicId(difficultyPojo.getTopicId());
        dCK.setQuestionId(difficultyPojo.getQuestionId());

        Difficulty difficulty = new Difficulty();
        difficulty.setId(dCK);
        difficulty.setDifficulty(difficultyPojo.getDifficulty());

        if (difficultyRepository.existsById(dCK)) return new ResponseEntity<>(HttpStatus.CONFLICT);

        if (!(questionRepository.existsById(difficultyPojo.getQuestionId()) && topicRepository.existsById(difficultyPojo.getTopicId()))) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        difficulty.setQuestion(questionRepository.findById(difficultyPojo.getQuestionId()).orElse(null));
        difficulty.setTopic(topicRepository.findById(difficultyPojo.getTopicId()).orElse(null));

        try {
            return new ResponseEntity<>(difficultyRepository.save(difficulty), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/difficulties")
    public @ResponseBody
    ResponseEntity<Iterable<Difficulty>> getAllDifficulties() {
        try {
            return new ResponseEntity<>(difficultyRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/topic/{topicId}/question/{questionId}/difficulty")
    public @ResponseBody
    ResponseEntity<Object> getDifficulty(@PathVariable Long questionId, @PathVariable Integer topicId) {
        DifficultyCompositeKey dCK = new DifficultyCompositeKey();
        dCK.setQuestionId(questionId);
        dCK.setTopicId(topicId);

        if (!difficultyRepository.existsById(dCK)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        try {
            return new ResponseEntity<>(difficultyRepository.findById(dCK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/topic/{topicId}/question/{questionId}/difficulty")
    public @ResponseBody
    ResponseEntity<Difficulty> updateDifficulty(@PathVariable Long questionId, @PathVariable Integer topicId, @RequestBody DifficultyPojo difficultyPojo) {
        DifficultyCompositeKey dCK = new DifficultyCompositeKey();
        dCK.setQuestionId(questionId);
        dCK.setTopicId(topicId);
        Optional<Difficulty> difficultyData = difficultyRepository.findById(dCK);

        if (difficultyData.isPresent()) {
            Difficulty difficulty = difficultyData.get();
            difficulty.setDifficulty(difficultyPojo.getDifficulty());
            return new ResponseEntity<>(difficultyRepository.save(difficulty), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/topic/{topicId}/question/{questionId}/difficulty")
    public @ResponseBody
    ResponseEntity<String> deleteDifficulty(@PathVariable Long questionId, @PathVariable Integer topicId) {
        DifficultyCompositeKey dCK = new DifficultyCompositeKey();
        dCK.setQuestionId(questionId);
        dCK.setTopicId(topicId);
        try {
            if (!difficultyRepository.existsById(dCK)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            difficultyRepository.deleteById(dCK);
            return new ResponseEntity<>("Difficulty with QuestionID " + questionId + " and TopicID " + topicId + " successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //POJOs
    private static class TopicPojo {
        Integer id;
        String name;

        public TopicPojo() {
        }

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
    }

    private static class ChangeUpperBoundPojo {
        float oldUpperBound;
        float newUpperBound;

        public ChangeUpperBoundPojo() {
        }

        public float getOldUpperBound() {
            return oldUpperBound;
        }

        public void setOldUpperBound(float oldUpperBound) {
            this.oldUpperBound = oldUpperBound;
        }

        public float getNewUpperBound() {
            return newUpperBound;
        }

        public void setNewUpperBound(float newUpperBound) {
            this.newUpperBound = newUpperBound;
        }
    }

    private static class DifficultyPojo {
        Long questionId;
        int topicId;
        float difficulty;

        public DifficultyPojo() {
        }

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public int getTopicId() {
            return topicId;
        }

        public void setTopicId(int topicId) {
            this.topicId = topicId;
        }

        public float getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(float difficulty) {
            this.difficulty = difficulty;
        }
    }

    private static class PoolUpperBoundPojo {
        int topicId;
        float upperBound;

        public PoolUpperBoundPojo() {
        }

        public int getTopicId() {
            return topicId;
        }

        public void setTopicId(int topicId) {
            this.topicId = topicId;
        }

        public float getUpperBound() {
            return upperBound;
        }

        public void setUpperBound(float upperBound) {
            this.upperBound = upperBound;
        }
    }
}
