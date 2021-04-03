package cat.v1.logic;

public class AdaptiveQuizResult extends QuizResult{
    private float currentDelta;
    private float currentCompetencyLevel;

    public AdaptiveQuizResult(float inputProficiencyLevel, float inputDelta) {
        this.currentCompetencyLevel=inputProficiencyLevel;
        this.currentDelta=inputDelta;
    }

    public float getCurrentDelta() {
        return currentDelta;
    }

    public void setCurrentDelta(float currentDelta) {
        this.currentDelta = currentDelta;
    }

    public float getCurrentCompetencyLevel() {
        return currentCompetencyLevel;
    }

    public void setCurrentCompetencyLevel(float currentCompetencyLevel) {
        this.currentCompetencyLevel = currentCompetencyLevel;
    }
}
