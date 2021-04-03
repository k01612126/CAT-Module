package cat.v1.logic;

import java.util.LinkedList;

public class ClassicQuiz extends Quiz {
    ClassicQuizResult classicQuizResult = new ClassicQuizResult();
    private int index = 0;

    public ClassicQuiz(LinkedList<QuizQuestion> questionSequence, float possiblePoints) {
        this.questions = questionSequence;
        classicQuizResult.setPossiblePoints(possiblePoints);
    }

    /**
     * Get the first Question of the Quiz
     * @return  QuizQuestion with Index 0
     */
    @Override
    public QuizQuestion getNextQuestion() {
        if (index > 0) return null;
        QuizQuestion q = this.questions.get(0);
        index++;
        return (q);
    }

    /**
     * Get the next Question of the Quiz
     * @param result    The achieved points on the previous question.
     * @return  The current QuizQuestion (Defined by Index)
     */
    @Override
    public QuizQuestion getNextQuestion(float result) {
        if (index <= this.questions.size()) {
            if (index == 0) {
                QuizQuestion q = this.questions.get(0);
                index++;
                return (q);
            }
            if (this.questions.get(index - 1).getDifficulty() >= result) {
                this.questions.get(index - 1).setResult(result);
                this.classicQuizResult.setAchievedPoints(this.classicQuizResult.getAchievedPoints() + result);
                //Deal with last question of quiz
                if (index == this.getQuestions().size()) {
                    this.setQuizFinished(true);
                    index++;
                    return (new QuizQuestion((long) -99, 0.0f));
                }
                QuizQuestion q = this.questions.get(index);
                index++;
                return (q);
            }
        }
        return (new QuizQuestion((long) -99, 0.0f));
    }

    @Override
    public ClassicQuizResult getQuizResult() {
        return (this.getCurrentQuizResult());
    }

    public ClassicQuizResult getCurrentQuizResult() {
        return classicQuizResult;
    }

    /**
     *  Set the current result of the Quiz
     * @param possiblePoints    The total amount of available Points in the quiz
     * @param achievedPoints    The total amount of achieved Points in the quiz
     */
    public void setClassicQuizResult(int possiblePoints, int achievedPoints) {
        this.classicQuizResult.setAchievedPoints(achievedPoints);
        this.classicQuizResult.setPossiblePoints(possiblePoints);
    }
}
