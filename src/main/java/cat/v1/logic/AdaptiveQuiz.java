package cat.v1.logic;

import cat.v1.entities.Question;
import cat.v1.r.RConnectionProvider;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.script.ScriptException;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class AdaptiveQuiz extends Quiz {
    public static final String SERVER_URI = "http://localhost:8080/catV1/";
    private int topicId;
    private float inputProficiencyLevel;
    private int maxNumberOfQuestions;
    private float measurementAccuracy;
    private NextItemSelectionMethod nextItemSelectionMethod;
    private String r_itemdiff, r_libFolder;
    private RConnectionProvider rConn;
    private TestEngine testEngine;
    private AdaptiveQuizResult currentQuizResult;
    private int indexStartBag = 0;
    private boolean questionsAvailable = true;
    private boolean firstQuestionIsSet = false;

    private RestTemplate restTemplate;

    public AdaptiveQuiz(int topicId, float inputProficiencyLevel, int maxNumberOfQuestions, float measurementAccuracy, String nextItemSelectionMethod, TestEngine testEngine) {
        this.topicId = topicId;
        this.inputProficiencyLevel = inputProficiencyLevel;
        this.maxNumberOfQuestions = maxNumberOfQuestions;
        this.measurementAccuracy = measurementAccuracy;
        this.nextItemSelectionMethod = NextItemSelectionMethod.valueOf(nextItemSelectionMethod);
        this.testEngine = testEngine;
        this.currentQuizResult = new AdaptiveQuizResult(inputProficiencyLevel, 1.0f);
        this.restTemplate = new RestTemplate();
        int index = 0;
        for (QuestionBag questionBag : this.testEngine.getTopic(topicId).getQuestionPools()) {
            if (questionBag.getUpperBound() >= inputProficiencyLevel) {
                this.indexStartBag = index;
                break;
            }
            index++;
        }
        initR();
    }

    /**
     * Verify if quizQuestion is up-to-date
     *
     * @param quizQuestion the quizQuestion to be verified
     * @return True if the quizQuestion is up-to-date
     */
    private boolean verifyActuality(QuizQuestion quizQuestion) {
        ResponseEntity<Question> response3 = this.restTemplate.getForEntity(SERVER_URI + "question/" + quizQuestion.getQuestionId(), Question.class);
        Question q = response3.getBody();
        return verifyActuality(q);
    }

    /**
     * Verify if the Question is up-to-date --> Comparison of the Last-Modified-Value with GeoGebra-Side
     *
     * @param q The Question to be verified
     * @return True if the question is up-to-date
     */
    private boolean verifyActuality(Question q) {
        //Create Request to GeoGebra
        RestTemplate restGeoGebra = new RestTemplate();
        String urlGeoGebra = "http://www.geogebra.org/api/json.php";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestbody = "{ \"request\": {\n" +
                "    \"-api\": \"1.0.0\",\n" +
                "    \"task\": {\n" +
                "      \"-type\": \"fetch\",\n" +
                "      \"fields\": {\n" +
                "        \"field\": [\n" +
                "          { \"-name\": \"id\" },\n" +
                "          { \"-name\": \"modified\" }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"filters\" : {\n" +
                "        \"field\": [\n" +
                "          { \"-name\":\"id\", \"#text\":\"" + q.getId() +
                "\" }\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        //Send Request to GeoGebra
        ResponseEntity<String> responseEntity = restGeoGebra.postForEntity(urlGeoGebra, requestbody, String.class);
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonBody = (JSONObject) parser.parse(responseEntity.getBody());
            JSONObject respones = (JSONObject) jsonBody.get("responses");
            JSONObject response = (JSONObject) respones.get("response");
            JSONObject item = (JSONObject) response.get("item");
            try {
                Long timestamp = (Long) item.getAsNumber("modified");
                LocalDateTime lastModified = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
                if (q.getLastModified().isBefore(lastModified)) {
                    System.out.println("Question " + q.getId() + " recently modified!");
                    this.restTemplate.put(SERVER_URI + "question/" + q.getId(), new QuestionPojo(q.getId(), false, lastModified), Question[].class);
                    q.setActive(false);
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Question " + q.getId() + " not Found");
                this.restTemplate.put(SERVER_URI + "question/" + q.getId(), new QuestionPojo(q.getId(), false, LocalDateTime.now()), Question[].class);
                q.setActive(false);
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    public NextItemSelectionMethod getNextItemSelectionMethod() {
        return nextItemSelectionMethod;
    }

    public void setNextItemSelectionMethod(NextItemSelectionMethod nextItemSelectionMethod) {
        this.nextItemSelectionMethod = nextItemSelectionMethod;
    }

    /**
     * Get the current QuizQuestion of the adaptive Quiz
     *
     * @return The current QuizQuestion.
     */
    @Override
    public QuizQuestion getNextQuestion() {
        if (this.questions.isEmpty()) {
            this.setNextQuestion(this.getInputProficiencyLevel()); //Set first question
        }
        return this.questions.getLast();
    }

    /**
     * Calculate the next Question based on previous Results
     *
     * @param result The percentage achieved on the previous question.
     */
    public void setNextQuestion(float result) {
        if (firstQuestionIsSet) {
            this.questions.getLast().setResult(result);
            this.requestCalculation();
        } else {
            this.questions.add(this.testEngine.getTopic(topicId).getQuestionPools().get(this.indexStartBag).getQuestions().get((int) Math.round((this.testEngine.getTopic(topicId).getQuestionPools().get(this.indexStartBag).getQuestions().size() - 1) * Math.random())));
            firstQuestionIsSet = true;
        }
    }

    /**
     * Get the next QuizQuestion of the adaptive Quiz
     *
     * @param result The percentage achieved on the previous question.
     * @return The next QuizQuestion
     */
    @Override
    public QuizQuestion getNextQuestion(float result) {
        //Set first Question if necessary.
        if (this.questions.isEmpty()) {
            this.setNextQuestion(this.getInputProficiencyLevel());
            return this.questions.getLast();
        }

        if (result > 1.0 || result < 0.0) {
            System.out.println("Result must be between 0.0 and 1.0");
            return null;
        }
        //Check if measurement accuracy threshold is reached and quiz will be finished or maximum amount of questions was reached and quiz will be finished
        if ((this.getCurrentQuizResult().getCurrentDelta() <= this.getMeasurementAccuracy()) || (this.questions.size() >= this.getMaxNumberOfQuestions())) {
            this.setQuizFinished(true);
            return (new QuizQuestion((long) -99, 0.0f));
        }
        this.setNextQuestion(result);

        //Check if Calculation of next Question was successful
        if (this.questionsAvailable) {
            return this.questions.getLast();
        } else {
            this.setQuizFinished(true);
            return (new QuizQuestion((long) -99, 0.0f));
        }
    }

    @Override
    public AdaptiveQuizResult getQuizResult() {
        return this.getCurrentQuizResult();
    }

    public AdaptiveQuizResult getCurrentQuizResult() {
        return currentQuizResult;
    }

    /**
     * Retrive Question From QuestionBag with Index
     *
     * @param arrayIndex The index of The QuestionBag
     * @return QuizQuestion from defined Bag
     */
    private QuizQuestion getQuestionFromBag(int arrayIndex) {
        //Check Input
        if (arrayIndex < 0 || arrayIndex > this.testEngine.getTopic(topicId).getQuestionPools().size()) {
            System.out.println("Invalid Index");
            return null;
        }

        List<QuizQuestion> list = this.testEngine.getTopic(topicId).getQuestionPools().get(arrayIndex).getQuestions(); //Get all Questions from Questionbag with arrayIndex
        int listSize = list.size();
        //Check if Questionbag is empty
        if (listSize == 0) {
            System.out.println("QuestionBag " + arrayIndex + "was empty switching to next Questionbag.");
            int nextArrayIndex = (arrayIndex + 1);
            if (nextArrayIndex >= this.testEngine.getTopic(topicId).getQuestionPools().size()) {
                System.out.println("Out of Questions!");
                return null;
            } else return getQuestionFromBag(nextArrayIndex);
        }
        //Select Random Question from within QuestionBag
        int index = (int) Math.round(Math.random() * (listSize - 1));
        QuizQuestion question = list.get(index);
        list.remove(index);
        if (verifyActuality(question)) {
            return question;
        } else {
            System.out.println("Question was not Up-To-Date!");
            return getQuestionFromBag(arrayIndex);
        }
    }

    /**
     * Get the Questionbag which contains questions with the wantedDifficulty
     *
     * @param wantedDifficulty Difficulty within Questionbag
     * @return Index of Questionbag
     */
    private int getArrayPositionFromWantedDifficulty(double wantedDifficulty) {
        for (int i = 0; i < this.testEngine.getTopic(topicId).getQuestionPools().size() - 1; i++) {
            if (wantedDifficulty < this.testEngine.getTopic(topicId).getQuestionPools().get(i).getUpperBound())
                return i;
        }
        return this.testEngine.getTopic(topicId).getQuestionPools().size() - 1; //If the wantedDifficulty is to high, the most difficult questionbag is chosen.
    }

    /**
     * Calculate next question in R and add it to questions
     */
    private void requestCalculation() {
        try {
            float oldCompetencyLevel = this.getCurrentQuizResult().getCurrentCompetencyLevel();
            float oldDelta = this.getCurrentQuizResult().getCurrentDelta();
            String rNameReturn = "next_item_parm";

            // Get script
            String RCodeScript = getRScript();

            // Execute R code and get result
            // [0] -> next item's difficulty, [1] -> current competence[, 2] -> Delta to result
            double[] result = rConn.execute(RCodeScript, rNameReturn);
            System.out.println(
                    " - Calculation result:\tNext item: " + result[0]
                            + "\tCurrent competence:\t" + result[1] + "\tDelta:\t"
                            + result[2]);
            this.getCurrentQuizResult().setCurrentCompetencyLevel((float) result[1]);
            this.getCurrentQuizResult().setCurrentDelta((float) (result[2]));

            QuizQuestion questionFromBag = getQuestionFromBag(getArrayPositionFromWantedDifficulty(result[0]));
            if (questionFromBag == null || result[2] < this.getMeasurementAccuracy()) {
                System.out.println("No more Questions available, or Measurement accuracy reached");
                this.setQuizFinished(true);
                this.questionsAvailable = false;
                return;
            } else {
                if (!this.questions.contains(questionFromBag)) {
                    this.questions.add(questionFromBag);
                    System.out.println("Next question difficulty: " + String.valueOf(questions.getLast().getDifficulty()));
                } else {
                    this.getCurrentQuizResult().setCurrentCompetencyLevel(oldCompetencyLevel);
                    this.getCurrentQuizResult().setCurrentDelta(oldDelta);
                    requestCalculation();
                }
            }
        } catch (ScriptException e) {
            System.out.println("Question could not be calculated.");
        }
    }

    /**
     * Initalize R --> R Autoextract, R Connection Provider, Load Questions into r_itemdiff
     */
    private void initR() {
        // Create the r_itemdiff String
        StringBuilder sb = new StringBuilder("item_diff <- c(");
        int bag = 0, item = 0;

        while (this.testEngine.getTopic(topicId).getQuestionPools().get(bag).getQuestions().size() == 0) {
            bag++;
        }
        sb.append(this.testEngine.getTopic(topicId).getQuestionPools().get(bag).getQuestions().get(0).getDifficulty()); // Get first item
        if (this.testEngine.getTopic(topicId).getQuestionPools().get(bag).getQuestions().size() > 1) {
            item = 1;
        } else {
            bag++;
        }
        while (bag < this.testEngine.getTopic(topicId).getQuestionPools().size()) {
            while (item < this.testEngine.getTopic(topicId).getQuestionPools().get(bag).getQuestions().size()) {
                sb.append(',').append(this.testEngine.getTopic(topicId).getQuestionPools().get(bag).getQuestions().get(item).getDifficulty());
                item++;
            }
            bag++;
            item = 0;
        }

        // Set R-variable itemdiff
        r_itemdiff = sb.append(')').toString();

        File path = new File("C:/Users/muehl/Documents/Offline Dateien/DUK/CAT/resources");
        if (path.exists()) {
            // CatR 3.4 auto extract
            try {
                path.mkdirs();
                String sourcePath = "C:/Users/muehl/Documents/Offline Dateien/DUK/CAT/resources/catR.zip";
                ZipFile zipFile = new ZipFile(sourcePath);
                zipFile.extractAll(path.getAbsolutePath());
                System.out.println("CatR extracted to " + path.getAbsolutePath());
            } catch (ZipException e) {
                System.out.println("There was an error while extracting catR to " + path.getAbsolutePath());
                path.delete();
                e.printStackTrace();
            }
        }
        r_libFolder = path.getAbsolutePath().replace("\\", "\\\\");
        // initialize RCaller
        rConn = new RConnectionProvider();
    }

    private String getRScript() {
        // Newline
        String nl = System.getProperty("line.separator");
        // Get R matrix (input)
        StringBuilder sb = new StringBuilder("double <- c(");
        Iterator<String> iterator =
                this.questions.stream().map(q -> q.getDifficulty() + ","
                        + (Math.abs(q.getResult()) < 0.01 ? "0" : "1")).iterator();
        if (iterator.hasNext()) {
            sb.append(iterator.next());
        }
        while (iterator.hasNext()) {
            sb.append(',').append(iterator.next());
        }
        String inputMatrix = sb.append(")").toString();
        String RScript = "library(catR, lib.loc=\""
                + r_libFolder
                + "\")"
                + nl
                + r_itemdiff
                + nl
                + inputMatrix
                + nl
                + "itembank <- unname(as.matrix(cbind(1, item_diff, 0, 1)))"
                + nl
                + "stellen <- 1:length(double)"
                + nl
                + "ungerade <- stellen[which(stellen %% 2 != 0)]"
                + nl
                + "gerade <- stellen[which(stellen %% 2 == 0)]"
                + nl
                + "response_pattern <- double[gerade]"
                + nl
                + "pre_items_diff <- as.matrix(double[ungerade],length(response_pattern)) "
                + nl
                + "previous_items <- as.matrix(rep(0, length(response_pattern), length(response_pattern)))"
                + nl
                + "for(i in 1:length(response_pattern)) {"
                + nl
                + "for(j in 1:nrow(itembank)) {"
                + nl
                + "if(pre_items_diff[i,1]==itembank[j,2]) (previous_items[i,1] <-j)"
                + nl
                + "}"
                + nl
                + "}"
                + nl
                + "select_next_item <- nextItem(itembank, x = response_pattern, out = previous_items, criterion = \"MPWI\")"
                + nl
                + "next_item_parm <- c(itembank[select_next_item$item,2], "
                + nl
                + "thetaEst(itembank[previous_items,], response_pattern),"
                + nl
                + "eapSem(thetaEst(itembank[previous_items,], response_pattern), itembank[previous_items,], response_pattern))";
        return RScript;
    }

    private static class QuestionPojo {
        Long id;
        boolean active;
        LocalDateTime lastModified;

        public QuestionPojo() {
        }

        public QuestionPojo(Long id, boolean active, LocalDateTime lastModified) {
            this.id = id;
            this.active = active;
            this.lastModified = lastModified;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public LocalDateTime getLastModified() {
            return lastModified;
        }

        public void setLastModified(LocalDateTime lastModified) {
            this.lastModified = lastModified;
        }
    }
}
