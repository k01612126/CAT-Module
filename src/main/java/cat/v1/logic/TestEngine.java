package cat.v1.logic;

import cat.v1.entities.PoolUpperBound;
import cat.v1.entities.Question;
import cat.v1.entities.Topic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestEngine {
    public static final String SERVER_URI = "http://localhost:8080/catV1/";
    private final ArrayList<Quiz> quizzes = new ArrayList<>(); // List of all currently running quizzes
    private final ArrayList<QuizTopic> topics = new ArrayList<>(); // List of all quiztopics (as defined in the database)
    private RestTemplate restTemplate;

    //*****General*****

    /**
     * Return the first question of the quiz with quizId
     *
     * @param quizId Identifies the Quiz
     * @return The NextQuestion which contains the quizId, the questionId and if the quiz is finished
     */
    public QuestionPojo getCurrentQuestion(int quizId) {
        try {
            for (Quiz q : quizzes) {
                if (q.getQuizId() == quizId)
                    return new QuestionPojo(quizId, q.getNextQuestion().getQuestionId(), q.isQuizFinished());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return the next question of the quiz with quizId
     *
     * @param quizId Identifies the Quiz
     * @param result The points achieved in the previous question. (For the adaptive quiz this is in percent)
     * @return The NextQuestion which contains the quizId, the questionId and if the quiz is finished
     */
    public QuestionPojo getNextQuestion(int quizId, float result) {
        for (Quiz q : quizzes) {
            if (q.getQuizId() == quizId)
                return new QuestionPojo(quizId, q.getNextQuestion(result).getQuestionId(), q.isQuizFinished());
        }
        return null;
    }

    /**
     * Return the result of the quiz with quizId
     *
     * @param quizId Identifies the quiz
     * @return The QuizResult (concrete Result differs between quizTypes)
     */
    public QuizResult getQuizResult(int quizId) {
        for (Quiz q : quizzes) {
            if (q.getQuizId() == quizId) return q.getQuizResult();
        }
        return null;
    }

    /**
     * Delete Quiz and Remove it from Quizlist
     *
     * @param quizId Identifies the quiz
     * @return True if the quiz was successfully deleted.
     */
    public boolean deleteQuiz(int quizId) {
        for (Quiz q : quizzes) {
            if (q.getQuizId() == quizId) {
                quizzes.remove(q);
                return q.deleteQuiz();
            }
        }
        return false;
    }

    /**
     * Get all Quizzes
     *
     * @return An ArrayList of all Quizzes
     */
    public ArrayList<Quiz> getQuizzes() {
        return quizzes;
    }

    //*****AdaptiveQuiz*****

    /**
     * Create a new Adaptive Quiz and return it
     *
     * @param topicId                 The Id of the topic chosen for the quiz
     * @param inputProficiencyLevel   The Start difficulty of the quiz
     * @param maxNumberOfQuestions    The maximum amount of questions for the quiz
     * @param measurementAccuracy     The threshold for the accuracy of the quiz result.
     * @param nextItemSelectionMethod The statistical method used for the selection of the next Question
     * @return The created quiz
     */
    public AdaptiveQuiz newQuiz(int topicId, float inputProficiencyLevel, int maxNumberOfQuestions, float measurementAccuracy, String nextItemSelectionMethod) {
        if (maxNumberOfQuestions < 1 || measurementAccuracy <= 0.0) return null;
        AdaptiveQuiz n = new AdaptiveQuiz(topicId, inputProficiencyLevel, maxNumberOfQuestions, measurementAccuracy, nextItemSelectionMethod, this);
        quizzes.add(n);
        return n;
    }

    /**
     * Get all QuizTopics of Test Engine
     *
     * @return An ArrayList of all Topics in the TestEngine
     */
    public ArrayList<QuizTopic> getTopics() {
        return topics;
    }

    /**
     * Get QuizTopic of Test Engine by its ID
     *
     * @param topicId The ID of the topic
     * @return The found QuizTopic
     */
    public QuizTopic getTopic(int topicId) {
        for (QuizTopic topic : topics) {
            if (topic.getId() == topicId) return topic;
        }
        return null;
    }

    /**
     * Load/Update QuizTopic with topicID from Database
     *
     * @param topicId The ID of the topic
     * @return the found QuizTopic
     */
    public QuizTopic setTopic(int topicId) {
        //Delete Topic if it already exists. So you can load it from database again.
        for (QuizTopic topic : topics) {
            if (topic.getId() == topicId) {
                System.out.println("Topic " + topic.getId() + " removed");
                topics.remove(topic);
                break;
            }
        }
        this.restTemplate = new RestTemplate();

        try {
            //Load all PoolUpperBounds of Topic
            ResponseEntity<PoolUpperBound[]> response = this.restTemplate.getForEntity(SERVER_URI + "topic/" + topicId + "/poolUpperBounds", PoolUpperBound[].class);
            PoolUpperBound[] responsePUB = response.getBody();
            ArrayList<QuestionBag> questionBags = new ArrayList<>();
            for (PoolUpperBound p : responsePUB) {
                questionBags.add(new QuestionBag(p.getUpperBound(), new ArrayList<>()));
            }
            questionBags.add(new QuestionBag(99.9f, new ArrayList<>())); //Add final questionBag which contains the most difficult questions

            //Load all Questions of Topic into Question Bags
            ResponseEntity<Question[]> responseQ = this.restTemplate.getForEntity(SERVER_URI + "topic/" + topicId + "/questions", Question[].class);
            Question[] topicQuestions = responseQ.getBody();
            for (Question tQ : topicQuestions) {
                if (tQ.getDifficulty(topicId) != -99 && tQ.isActive()) {
                    int i = -1;
                    for (QuestionBag qb : questionBags) {
                        i++;
                        if (tQ.getDifficulty(topicId) <= qb.getUpperBound()) {
                            qb.setQuestion(new QuizQuestion(tQ.getId(), tQ.getDifficulty(topicId)));
                            System.out.println("Question " + tQ.getId() + " with difficulty " + tQ.getDifficulty(topicId) + " added to Bag " + i);
                            break;
                        }
                    }
                }
            }

            //Create Topic and add filled Question Bags
            QuizTopic qT = new QuizTopic(topicId, questionBags);
            this.topics.add(qT);
            return (qT);
        } catch (Exception e) {
            return (null);
        }
    }

    /**
     * Load/Update all QuizTopics from database
     *
     * @return An ArrayList of all Topics in the TestEngine
     */
    public ArrayList<QuizTopic> setTopics() {
        this.restTemplate = new RestTemplate();
        ResponseEntity<Topic[]> response = this.restTemplate.getForEntity(SERVER_URI + "topics", Topic[].class);
        Topic[] responseT = response.getBody();
        for (Topic t : responseT) {
            this.setTopic(t.getId());
        }
        return getTopics();
    }

    /**
     * Delete   Topic with topicId from TestEngine
     *
     * @param topicId The ID identifying the Topic to be deleted.
     * @return True if the topic was successfully deleted
     */
    public boolean deleteTopic(int topicId) {
        for (QuizTopic topic : topics) {
            if (topic.getId() == topicId) {
                System.out.println("Topic " + topic.getId() + " removed");
                topics.remove(topic);
                return true;
            }
        }
        return false;
    }

    //*****Classic Quiz*****

    /**
     * Create a new Classic Quiz and return it
     *
     * @param quizQuestions  Ordered List which contains the questions of the quiz
     * @param possiblePoints The amount of achievable Points
     * @return The created quiz
     */
    public ClassicQuiz newQuiz(LinkedList<QuizQuestion> quizQuestions, float possiblePoints) {
        ClassicQuiz n = new ClassicQuiz(quizQuestions, possiblePoints);
        quizzes.add(n);
        return n;
    }
}
