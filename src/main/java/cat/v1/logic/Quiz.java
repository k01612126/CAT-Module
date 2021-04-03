package cat.v1.logic;

import java.util.LinkedList;

public abstract class Quiz {
    private static int qID = 0;
    LinkedList<QuizQuestion> questions;
    final int quizId;
    private boolean quizFinished;

    public Quiz() {
        qID++;
        this.quizId = qID;
        this.questions = new LinkedList<>();
        this.quizFinished = false;
    }

    public int getQuizId() {
        return quizId;
    }

    public LinkedList<QuizQuestion> getQuestions() {
        return questions;
    }

    public abstract QuizQuestion getNextQuestion();

    public abstract QuizQuestion getNextQuestion(float result);

    public boolean isQuizFinished() {
        return quizFinished;
    }

    public void setQuizFinished(boolean quizFinished) {
        this.quizFinished = quizFinished;
    }

    public boolean deleteQuiz() {
        try {
            this.finalize();
            return true;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    public abstract QuizResult getQuizResult();
}
