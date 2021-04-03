package cat.v1.logic;

public class QuestionPojo {
    private int quizId;
    private Long questionId;
    boolean quizFinished;

    public QuestionPojo(int quizId, Long questionId, boolean quizFinished) {
        this.quizId = quizId;
        this.questionId = questionId;
        this.quizFinished = quizFinished;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public boolean isQuizFinished() {
        return quizFinished;
    }

    public void setQuizFinished(boolean quizFinished) {
        this.quizFinished = quizFinished;
    }
}
