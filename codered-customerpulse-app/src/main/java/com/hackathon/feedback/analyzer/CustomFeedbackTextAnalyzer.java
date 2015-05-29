package com.hackathon.feedback.analyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class CustomFeedbackTextAnalyzer {

	public FeedbackText findSentiment(String line) {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		int mainSentiment = 0;
		if (line != null && line.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(line);

			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				
				 Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
	                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	                
	                
	                
	               traverseTree(tree);
			/*	Tree tree = sentence.get(TreeAnnotation.class);

				TreebankLanguagePack tlp = new PennTreebankLanguagePack();
				
				GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
				
				GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
				
				System.out.println(gs.getNodes());
				
				
				Collection<TypedDependency> td = gs
						.typedDependenciesCollapsed();
				System.out.println("tes23232t" + td);

				Object[] list = td.toArray();
				System.out.println(list.length);
				TypedDependency typedDependency;
				for (Object object : list) {
					typedDependency = (TypedDependency) object;
					System.out.println("Depdency Name"
							+ typedDependency.dep().nodeString() + " :: "
							+ "Node" + typedDependency.reln());
				}*/

			}
		}
		/*
		 * if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
		 * return null; }
		 */
		FeedbackText FeedbackText = new FeedbackText(line, toCss(mainSentiment));
		return FeedbackText;

	}

	private void traverseTree(Tree tree) {
		System.out.println("Parent :: "+tree.label().value());
		for(int i = 1; i <=tree.getChildrenAsList().size(); i++){
			Tree childNode = tree.getChild(i);
			System.out.println("Child Node :"+ childNode.label().value());
			traverseTree(childNode);
		}
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

	public static void generateOutputText(String path) {
		Path inputFilePath = Paths
				.get("D:\\github\\hackathon0528\\codered-customerpulse-app\\src\\main\\resources\\codered",
						path);
		Charset charset = Charset.forName("US-ASCII");
		StringBuilder outStr = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(inputFilePath,
				charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				CustomFeedbackTextAnalyzer sentimentAnalyzer = new CustomFeedbackTextAnalyzer();
				FeedbackText output = sentimentAnalyzer.findSentiment(line);
				outStr.append(output.getCssClass()).append(
						System.getProperty("line.separator"));
			}
			Path outFilePath = Paths
					.get("D:\\github\\hackathon0528\\codered-customerpulse-app\\src\\main\\resources\\codered",
							"output.txt");
			try (BufferedWriter writer = Files.newBufferedWriter(outFilePath,
					charset)) {
				writer.write(outStr.toString(), 0, outStr.toString().length());
			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);
			}

		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

	}

	public static void main(String[] args) {
		generateOutputText("inputsample.txt");
	}

}