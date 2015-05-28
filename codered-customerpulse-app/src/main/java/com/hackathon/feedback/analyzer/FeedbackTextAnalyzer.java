package com.hackathon.feedback.analyzer;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class FeedbackTextAnalyzer {

    public FeedbackText findSentiment(String line) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        /*if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }*/
        FeedbackText FeedbackText = new FeedbackText(line, toCss(mainSentiment));
        return FeedbackText;

    }

    private String toCss(int sentiment) {
        switch (sentiment) {
        case 4:
            return "VERY POSITIVE";
        case 3:
            return "POSITIVE";
        case 2:
            return "NEUTRAL";
        case 1:
            return "NEGATIVE";
        case 0:
            return "VERY NEGATIVE";
        default:
            return "";
        }
    }

    public static void main(String[] args) {
    	
        FeedbackTextAnalyzer sentimentAnalyzer = new FeedbackTextAnalyzer();
        FeedbackText FeedbackText1 = sentimentAnalyzer.findSentiment("Blazing fast internet speeds... I'm blown away ");
        System.out.println("FeedbackText1"+FeedbackText1);
        
        FeedbackText FeedbackText2 = sentimentAnalyzer.findSentiment("I have been talking to 5 agents so far, still no end to my pain... phone can't be dead for 10 days!!! ");
        System.out.println("FeedbackText2" +FeedbackText2);
        
        FeedbackText FeedbackText3 = sentimentAnalyzer.findSentiment("Been a loyal customer for 3 years enjoying the service, the last three months has been horrible ");
        System.out.println("FeedbackText3"+FeedbackText3);
        
        FeedbackText FeedbackText4 = sentimentAnalyzer.findSentiment("Network quality is great but certainly not value for money.... I can get better deals elsewhere ");
        System.out.println("FeedbackText4" +FeedbackText4);
        
        
        FeedbackText FeedbackText5 = sentimentAnalyzer.findSentiment("Summer is finally here ");
        System.out.println("FeedbackText5"+FeedbackText5);
        
        FeedbackText FeedbackText6 = sentimentAnalyzer.findSentiment("Amazing picture quality... HD TV Rocks");
        System.out.println("FeedbackText6" +FeedbackText6);
        
        FeedbackText FeedbackText7 = sentimentAnalyzer.findSentiment("The entire ordering process was a breeze.... Loved it ");
        System.out.println("FeedbackText7"+FeedbackText7);
        
        FeedbackText FeedbackText8 = sentimentAnalyzer.findSentiment("Arrived in a timely fashion. Plugged it in and it works like a charm! No more annoying beeping from the old battery. After doing some homework, we found this price extremely affordable.  ");
        System.out.println("FeedbackText8" +FeedbackText8);
        
        FeedbackText FeedbackText9 = sentimentAnalyzer.findSentiment("I took the day off yesterday for the installation, whole family was around and the technician did not show up. How do I cancel the service now? ");
        System.out.println("FeedbackText9"+FeedbackText9);
        
        FeedbackText FeedbackText10 = sentimentAnalyzer.findSentiment("It's been a crazy day... traffic is horrible ");
        System.out.println("FeedbackText10" +FeedbackText10);
    }
}