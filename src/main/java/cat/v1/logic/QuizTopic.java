package cat.v1.logic;

import java.util.ArrayList;

public class QuizTopic {
    private int id;
    private ArrayList<QuestionBag> questionBags;

    public QuizTopic(int id, ArrayList<QuestionBag> questionBags) {
        this.id = id;
        this.questionBags = questionBags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<QuestionBag> getQuestionPools() {
        return questionBags;
    }

    public void setQuestionPools(ArrayList<QuestionBag> questionBags) {
        this.questionBags = questionBags;
    }
}
