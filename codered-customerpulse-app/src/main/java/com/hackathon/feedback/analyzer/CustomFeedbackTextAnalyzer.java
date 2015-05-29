package com.hackathon.feedback.analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

import com.hackathon.feedback.customizer.POSTagger;

public class CustomFeedbackTextAnalyzer {

	public FeedbackText findSentiment(String line, int id) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
        	try {
				POSTagger.insertRecord(line, id);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        	try {
				new POSTagger().setPOSTagger(line, id);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			return "POSITIVE";
		case 3:
			return "POSITIVE";
		case 2:
			return "NEUTRAL";
		case 1:
			return "NEGATIVE";
		case 0:
			return "NEGATIVE";
		default:
			return "NEUTRAL";
		}
	}

	public static void generateOutputText(String path) throws SQLException {
		Path inputFilePath = Paths
				.get("D:\\github\\hackathon0528\\codered-customerpulse-app\\src\\main\\resources\\codered",
						path);
		Charset charset = Charset.forName("US-ASCII");
		StringBuilder outStr = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(inputFilePath,
				charset)) {
			String line = null;
			int counterId = 1;
			// Delete Records
			POSTagger.delteRecord();
			while ((line = reader.readLine()) != null) {
				CustomFeedbackTextAnalyzer sentimentAnalyzer = new CustomFeedbackTextAnalyzer();
				FeedbackText output = sentimentAnalyzer.findSentiment(line, counterId++);
				outStr.append(output.getCssClass()).append(
						System.getProperty("line.separator"));
			}
			// Call  Stored Procedure
			try {
				POSTagger.callStoredProcedure();
			} catch (Exception e) {
				e.printStackTrace();
			}
			POSTagger.generateOutput();
			
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	public static void main(String[] args) {
		try {
			generateOutputText("inputsample.txt");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}