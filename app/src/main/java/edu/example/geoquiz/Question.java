package edu.example.geoquiz;

public class Question {

    private int textResId, answer;
    private boolean isAnswerTrue;

    public Question(int textResId, boolean isAnswerTrue, int answer) {
        this.textResId = textResId;
        this.isAnswerTrue = isAnswerTrue;
        this.answer = answer;
    }

    public int getTextResId() {
        return textResId;
    }

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }

    public boolean isAnswerTrue() {
        return isAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        isAnswerTrue = answerTrue;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
