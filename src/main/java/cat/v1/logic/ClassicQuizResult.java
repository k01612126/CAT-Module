package cat.v1.logic;

public class ClassicQuizResult extends QuizResult{
    private float possiblePoints=0;
    private float achievedPoints=0;

    public float getPossiblePoints() {
        return possiblePoints;
    }

    public void setPossiblePoints(float possiblePoints) {
        this.possiblePoints = possiblePoints;
    }

    public float getAchievedPoints() {
        return achievedPoints;
    }

    public void setAchievedPoints(float achievedPoints) {
        this.achievedPoints = achievedPoints;
    }
}
