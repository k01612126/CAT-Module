package cat.v1.logic;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;

@Controller
@RequestMapping(path = "/catV1")
public class QuizController {
    private TestEngine testEngine = new TestEngine();

    /**
     * Create Adaptive Quiz
     * @param adaptiveQuizPojo  Configuration of adaptive Quiz
     * @return  Pojo representing the new Quiz
     */
    @PostMapping(path = "/quiz/adaptive")
    public @ResponseBody
    ResponseEntity<AdaptiveQuizPojo> addNewQuiz(@RequestBody AdaptiveQuizPojo adaptiveQuizPojo) {
        try {
            AdaptiveQuiz adaptiveQuiz = testEngine.newQuiz(adaptiveQuizPojo.getTopicId(), adaptiveQuizPojo.getInputProficiencyLevel(), adaptiveQuizPojo.getMaxNumberOfQuestions(), adaptiveQuizPojo.getMeasurementAccuracy(), adaptiveQuizPojo.getNextItemSelectionMethod());
            AdaptiveQuizPojo adaptiveQuizPojoResponse = new AdaptiveQuizPojo(adaptiveQuiz);
            return new ResponseEntity<>(adaptiveQuizPojoResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Load QuestionBags of Topic from database into testengine
     * @param id    Identifier of the topic
     * @return  the topic incl. all questionbags
     */
    @PostMapping(path = "/quizTopic/{id}/questionBags")
    public @ResponseBody
    ResponseEntity<QuizTopic> loadTopicQuestionBags(@PathVariable Integer id) {
        QuizTopic quizTopic = testEngine.setTopic(id);
        if (quizTopic != null) {
            return new ResponseEntity<>(quizTopic, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Load QuestionBags of Topic from database into testengine
     * @param id    Identifier of the topic
     * @return  the topic incl. all questionbags
     */
    @PutMapping(path = "/quizTopic/{id}/questionBags")
    public @ResponseBody
    ResponseEntity<QuizTopic> updateTopicQuestionBags(@PathVariable Integer id) {
        QuizTopic quizTopic = testEngine.setTopic(id);
        if (quizTopic != null) {
            return new ResponseEntity<>(quizTopic, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Load QuestionBags of all Topics from database into testengine
     * @return the topics incl. all questionbags
     */
    @PostMapping(path = "/quizTopics/questionBags")
    public @ResponseBody
    ResponseEntity<Iterable<QuizTopic>> loadAllTopicQuestionBags() {
        ArrayList<QuizTopic> quizTopics = testEngine.setTopics();
        if (quizTopics != null && (!quizTopics.isEmpty())) {
            return new ResponseEntity<>(quizTopics, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Load QuestionBags of all Topics from database into testengine
     * @return the topics incl. all questionbags
     */
    @PutMapping(path = "/quizTopics/questionBags")
    public @ResponseBody
    ResponseEntity<Iterable<QuizTopic>> updateAllTopicQuestionBags() {
        ArrayList<QuizTopic> quizTopics = testEngine.setTopics();
        if (quizTopics != null && (!quizTopics.isEmpty())) {
            return new ResponseEntity<>(quizTopics, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all QuizTopics
     * @return all QuizTopics
     */
    @GetMapping(path = "/quizTopics")
    public @ResponseBody
    ResponseEntity<Iterable<QuizTopic>> getAllTopics() {
        ArrayList<QuizTopic> quizTopics = testEngine.getTopics();
        if (quizTopics != null && (!quizTopics.isEmpty())) {
            return new ResponseEntity<>(quizTopics, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get Topic by ID
     * @param id  Identifier of the topic
     * @return  The QuizTopic
     */
    @GetMapping(path = "/quizTopic/{id}")
    public @ResponseBody
    ResponseEntity<QuizTopic> getTopic(@PathVariable Integer id) {
        QuizTopic quizTopic = testEngine.getTopic(id);
        if (quizTopic != null) {
            return new ResponseEntity<>(quizTopic, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete Topic by id
     * @param id  Identifier of the topic
     * @return  OK if Topic was successfully deleted
     */
    @DeleteMapping(path = "/quizTopic/{id}")
    public @ResponseBody
    ResponseEntity<String> deleteTopic(@PathVariable Integer id) {
        if (testEngine.deleteTopic(id)) {
            return new ResponseEntity<>("QuizTopic with id=" + id + " was successfully deleted!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("QuizTopic with id=" + id + " was not Found!", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete Quiz by ID
     * @param id    Identifier of the quiz
     * @return  OK if Quiz was successfully deleted
     */
    @DeleteMapping(path = "/quiz/{id}")
    public @ResponseBody
    ResponseEntity<String> deleteQuiz(@PathVariable Integer id) {
        if (testEngine.deleteQuiz(id)) {
            return new ResponseEntity<>("Quiz with id=" + id + " was successfully deleted!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Quiz with id=" + id + " was not Found!", HttpStatus.NOT_FOUND);
        }
    }

    //Classic Quiz

    /**
     * Create Adaptive Quiz
     * @param classicQuizPojo   Configuration of classic Quiz
     * @return  Pojo representing the new Quiz
     */
    @PostMapping(path = "/quiz/classic")
    public @ResponseBody
    ResponseEntity<ClassicQuizPojo> addNewQuiz(@RequestBody ClassicQuizPojo classicQuizPojo) {
        try {
            LinkedList<QuizQuestion> quizQuestions = new LinkedList<>();
            float possiblePoints = 0;
            for (ClassicQuizPojo.ClassicQuestionPojo q : classicQuizPojo.getQuestions()) {
                if (q.getId() != null) {
                    quizQuestions.add(new QuizQuestion(q.getId(), q.getAvailablePoints()));
                    possiblePoints = possiblePoints + q.getAvailablePoints();
                }
            }
            ClassicQuiz classicQuiz = testEngine.newQuiz(quizQuestions, possiblePoints);
            return new ResponseEntity<>(new ClassicQuizPojo(classicQuiz), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //General

    /**
     * Get the Result of a Quiz by ID
     * @param id    Identifier of the quiz
     * @return  The QuizResult (differs between quizzes)
     */
    @GetMapping(path = "/quiz/{id}/result")
    public @ResponseBody
    ResponseEntity<QuizResult> getQuizResult(@PathVariable Integer id) {
        QuizResult quizResult = testEngine.getQuizResult(id);
        if (quizResult != null) {
            return new ResponseEntity<>(quizResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all Quizzes
     * @return  An List of Quizzes
     */
    @GetMapping(path = "/quizzes")
    public @ResponseBody
    ResponseEntity<ArrayList<Quiz>> getAllQuizzes() {
        try {
            return new ResponseEntity<>(testEngine.getQuizzes(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the currentquestion of a Quiz
     * @param id    Identifier of the quiz
     * @return  The QuestionPojo containing the last question administered
     */
    @GetMapping(path = "/quiz/{id}/currentquestion")
    public @ResponseBody
    ResponseEntity<QuestionPojo> getCurrentQuestion(@PathVariable Integer id) {
        System.out.println("CURRENT QUESTION---------QUIZ ID= " + id + "--------------------");
        QuestionPojo questionPojo = testEngine.getCurrentQuestion(id);
        if(questionPojo!=null){
            return new ResponseEntity<>(questionPojo, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get the next Question of a Quiz
     * @param id    Identifier of the quiz
     * @param questionResultPojo    The Pojo containing the results of the last question
     * @return  The QuestionPojo containing the next question to be administered
     */
    @GetMapping(path = "/quiz/{id}/question")
    public @ResponseBody
    ResponseEntity<QuestionPojo> getNextQuestion(@PathVariable Integer id, @RequestBody QuestionResultPojo questionResultPojo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println("NEXT QUESTION---------QUIZ ID= " + id + "--------------------");
        try {
            QuestionPojo nextQuestion = testEngine.getNextQuestion(id, questionResultPojo.getResult());
            if (nextQuestion != null) {
                stopWatch.stop();
                //System.out.println("QuizController - This took "+stopWatch.getTime() +"Miliseconds");
                return new ResponseEntity<>(nextQuestion, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //POJOs
    private static class AdaptiveQuizPojo {
        int quizId;
        int topicId;
        float inputProficiencyLevel;
        int maxNumberOfQuestions;
        float measurementAccuracy;
        String nextItemSelectionMethod;

        public AdaptiveQuizPojo(AdaptiveQuiz adaptiveQuiz) {
            this.quizId = adaptiveQuiz.getQuizId();
            this.topicId = adaptiveQuiz.getTopicId();
            this.inputProficiencyLevel = adaptiveQuiz.getInputProficiencyLevel();
            this.maxNumberOfQuestions = adaptiveQuiz.getMaxNumberOfQuestions();
            this.measurementAccuracy = adaptiveQuiz.getMeasurementAccuracy();
            this.nextItemSelectionMethod = adaptiveQuiz.getNextItemSelectionMethod().toString();
        }

        public AdaptiveQuizPojo() {
        }

        public int getTopicId() {
            return topicId;
        }

        public void setTopicId(int topicId) {
            this.topicId = topicId;
        }

        public float getInputProficiencyLevel() {
            return inputProficiencyLevel;
        }

        public void setInputProficiencyLevel(float inputProficiencyLevel) {
            this.inputProficiencyLevel = inputProficiencyLevel;
        }

        public int getMaxNumberOfQuestions() {
            return maxNumberOfQuestions;
        }

        public void setMaxNumberOfQuestions(int maxNumberOfQuestions) {
            this.maxNumberOfQuestions = maxNumberOfQuestions;
        }

        public float getMeasurementAccuracy() {
            return measurementAccuracy;
        }

        public void setMeasurementAccuracy(float measurementAccuracy) {
            this.measurementAccuracy = measurementAccuracy;
        }

        public String getNextItemSelectionMethod() {
            return nextItemSelectionMethod;
        }

        public void setNextItemSelectionMethod(String nextItemSelectionMethod) {
            this.nextItemSelectionMethod = nextItemSelectionMethod;
        }

        public int getQuizId() {
            return quizId;
        }

        public void setQuizId(int quizId) {
            this.quizId = quizId;
        }
    }

    private static class ClassicQuizPojo {
        int quizId;
        LinkedList<ClassicQuestionPojo> questions = new LinkedList<>();

        public ClassicQuizPojo() {
        }

        public ClassicQuizPojo(ClassicQuiz classicQuiz) {
            this.quizId = classicQuiz.getQuizId();
            for (QuizQuestion q : classicQuiz.getQuestions()) {
                questions.add(new ClassicQuestionPojo(q.getQuestionId(), q.getDifficulty()));
            }
        }

        public int getQuizId() {
            return quizId;
        }

        public void setQuizId(int quizId) {
            this.quizId = quizId;
        }

        public LinkedList<ClassicQuestionPojo> getQuestions() {
            return questions;
        }

        public void setQuestions(LinkedList<ClassicQuestionPojo> questions) {
            this.questions = questions;
        }

        private static class ClassicQuestionPojo {
            Long id;
            float availablePoints;

            public ClassicQuestionPojo() {
            }

            public ClassicQuestionPojo(Long id, float availablePoints) {
                this.id = id;
                this.availablePoints = availablePoints;
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public float getAvailablePoints() {
                return availablePoints;
            }

            public void setAvailablePoints(float availablePoints) {
                this.availablePoints = availablePoints;
            }
        }
    }

    private static class QuestionResultPojo {
        Long quizId;
        float result;

        public QuestionResultPojo() {
        }

        public Long getQuizId() {
            return quizId;
        }

        public void setQuizId(Long quizId) {
            this.quizId = quizId;
        }

        public float getResult() {
            return result;
        }

        public void setResult(float result) {
            this.result = result;
        }
    }
}
