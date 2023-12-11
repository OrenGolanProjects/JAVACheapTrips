package com.orengolan.cheaptrips.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentClass;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The {@code SentimentAnalyzer} service class leverages the Stanford NLP library to perform sentiment analysis on textual content.
 * It utilizes a pre-trained model to analyze the sentiment of a given text and provides a sentiment score ranging from 1 (very negative) to 5 (very positive).
 *
 * Key Features:
 * - Integrates with the Stanford NLP library for natural language processing and sentiment analysis.
 * - Utilizes a post-construct initialization method to set up the required NLP pipeline and sentiment value mappings.
 * - Offers a method to analyze the sentiment of a given text, returning a normalized sentiment score based on the input text's sentiment.
 * - The sentiment score is calculated as an average over all sentences in the text, providing an overall sentiment assessment.
 *
 * Example Usage:
 * The class is used within the application to assess the sentiment of user-generated content, feedback, or any textual data.
 * Developers can incorporate this service to gain insights into the emotional tone of text, aiding in sentiment-aware decision-making.
 *
 * Note: Sentiment analysis can be valuable for understanding user sentiments, customer feedback, and making informed decisions based on textual data.
 */
@Service
public class SentimentAnalyzer {
    StanfordCoreNLP nlp;
    Map<String, Double> sentimentValues = new HashMap<>();

    @PostConstruct
    public void init() {
        Properties nlpProps = new Properties();
        nlpProps.put("annotators", "tokenize, ssplit, parse, sentiment");
        nlp = new StanfordCoreNLP(nlpProps);
        sentimentValues.put("Very negative", 1d);
        sentimentValues.put("Negative", 2d);
        sentimentValues.put("Neutral",3d);
        sentimentValues.put("Positive",4d);
        sentimentValues.put("Very positive",5d);

    }

    public Double analyze(String text) {
        Annotation annotation = nlp.process(text);
        List<CoreMap> sentences =  annotation.get(CoreAnnotations.SentencesAnnotation.class);
        return   sentences.stream()
                .map(sentence->sentence.get(SentimentClass.class))
                .map(sentimentStr->sentimentValues.get(sentimentStr))
                .mapToDouble(x->x).sum()
                / (Double.parseDouble(String.valueOf(sentences.size())));
    }
}
