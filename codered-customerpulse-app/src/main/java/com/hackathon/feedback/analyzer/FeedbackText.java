package com.hackathon.feedback.analyzer;


public class FeedbackText {

    private String line;
    private String cssClass;

    public FeedbackText() {
    }

    public FeedbackText(String line, String cssClass) {
        super();
        this.line = line;
        this.cssClass = cssClass;
    }

    public String getLine() {
        return line;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return "TweetWithSentiment [line=" + line + ", cssClass=" + cssClass + "]";
    }

}
