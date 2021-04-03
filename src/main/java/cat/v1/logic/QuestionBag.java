package cat.v1.logic;

import java.util.ArrayList;

public class QuestionBag {
    private int id;
    private float upperBound;
    private ArrayList<QuizQuestion> questions;

    public QuestionBag(float upperBound, ArrayList<QuizQuestion> questions) {
        this.upperBound = upperBound;
        this.questions = questions;
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public ArrayList<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuizQuestion> questions) {
        this.questions = questions;
    }

    public void setQuestion(QuizQuestion question){
        this.questions.add(question);
    }
}
