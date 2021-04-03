package cat.v1.logic;

public class QuizQuestion {
    private Long questionId;
    private float difficulty;
    private float result;

    public QuizQuestion(Long questionId, float difficulty) {
        this.questionId = questionId;
        this.difficulty = difficulty;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }
}
