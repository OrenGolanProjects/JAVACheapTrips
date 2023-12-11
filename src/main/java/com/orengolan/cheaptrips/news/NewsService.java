package com.orengolan.cheaptrips.news;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.service.SentimentAnalyzer;
import com.orengolan.cheaptrips.util.API;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.Date;


/**
 * The {@code NewsService} class is responsible for handling news-related operations, including fetching news from an external API,
 * performing sentiment analysis, and interacting with the MongoDB database to store and retrieve news data.
 *
 * Key Features:
 * - {@code sentimentAnalyzer}: Performs sentiment analysis on news content.
 * - {@code objectMapper}: Handles JSON serialization and deserialization.
 * - {@code newsRepository}: Serves as the data access layer for interacting with the MongoDB database for news entities.
 * - {@code api}: Provides utility methods for making HTTP requests to external APIs.
 * - {@code newsEndpoint} and {@code newsToken}: Configuration properties for the external news API endpoint and authentication token.
 *
 * Methods:
 * - {@code getNewsStream}: Fetches news data from an external API based on the specified city name, page size, and last day.
 * - {@code fetchNewsStream}: Parses the JSON response from the API, performs sentiment analysis, and constructs a list of news items.
 * - {@code saveNews}: Persists the news data to the MongoDB database, including setting expiration time and updating news count.
 * - {@code getNews}: Retrieves news data for a given city name, either from the database or by fetching and saving new data.
 * - {@code getAllNews}: Retrieves all news entries stored in the database.
 * - {@code deleteAllNews}: Deletes all news entries from the database.
 *
 * Example Usage:
 * The class is used as a service in a Spring application to manage news-related functionalities, ensuring the retrieval
 * and storage of news data in the MongoDB database for different cities.
 *
 * Note: Proper configuration of external API endpoints, authentication tokens, and MongoDB connection settings is required.
 */
@Service
public class NewsService {

    private final Logger logger = Logger.getLogger(NewsService.class.getName());
    private final SentimentAnalyzer sentimentAnalyzer;
    private final ObjectMapper objectMapper;
    private final NewsRepository newsRepository;
    private final API api;

    private final String newsEndpoint;
    private final String newsToken;

    public NewsService(Dotenv dotenv, SentimentAnalyzer sentimentAnalyzer, ObjectMapper objectMapper, NewsRepository newsRepository, API api) {
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.objectMapper = objectMapper;
        this.newsRepository = newsRepository;
        this.api = api;
        this.newsEndpoint = dotenv.get("news_ENDPOINT");
        this.newsToken = dotenv.get("news_TOKEN");
    }

    private String getNewsStream(String cityName, Integer pageSize,String lastDay) throws IOException {

        logger.info("NewsService>>  getNewsStream: Start method.");
        logger.info("NewsService>>  getNewsStream: Data: cityName:"+cityName+" , pageSize: "+pageSize+" ,lastDay:"+lastDay);

        String URL = newsEndpoint+cityName+"&from="+lastDay+"&sortBy=popularity" +"&pageSize="+pageSize+"&language=en&apiKey="+ newsToken;
        return this.api.buildAndExecuteRequest(URL,null);
    }

    private List<News.news> fetchNewsStream(String cityName, Integer pageSize,String lastDay,List<News.news> newsList) throws IOException {

        logger.info("NewsService>>  fetchNewsStream: Start method.");
        String json = getNewsStream(cityName,pageSize,lastDay);
        JsonNode rootNode = this.objectMapper.readTree(json);

        logger.info("NewsService>>  fetchNewsStream: Found "+rootNode.size()+" news.");
        for (JsonNode article: rootNode.get("articles")){

            String sourceName = article.path("source").get("name").asText();
            String author = article.get("author").asText();
            String title = article.get("title").asText();
            String description = article.get("description").asText();
            String url = article.get("url").asText();
            String urlToImage = article.get("urlToImage").asText();
            String publishedAt = article.get("publishedAt").asText();
            String content = article.get("content").asText();

            if(title.isEmpty() || title.equals("[Removed]")){
                continue;
            }
            Double score= this.sentimentAnalyzer.analyze(content);
            News.news newsItem = new News.news(sourceName,author,title,description,url,urlToImage,publishedAt,content,score);
            newsList.add(newsItem);
        }
        logger.info("NewsService>>  fetchNewsStream: End method.");
        return newsList;
    }

    public void saveNews(News news){
        logger.info("NewsService>>  saveNews: Start method.");
        news.setExpireAt(new Date(System.currentTimeMillis() + 604800000));
        news.setNewsListCount(news.getNewsList().size());
        this.newsRepository.insert(news);
        logger.info("NewsService>>  saveNews: End method.");
    }

    public News getNews(String cityName, Integer pageSize) throws IOException {
        logger.info("NewsService>>  getNews: Start method.");
        logger.info("NewsService>>  getNews: Data: cityName:"+cityName+" , pageSize: "+pageSize);
        if(cityName.length()>10){
            throw new IllegalArgumentException("Invalid city name, City name must be until 10 letters.");
        }

        News existingNews = this.newsRepository.findByCityName(cityName);
        if(existingNews != null){
            logger.info("NewsService>>  getNews: Found News for city: "+cityName);
            logger.info("NewsService>>  getNews: End method.");
            return existingNews;
        }

        logger.info("NewsService>>  getNews: Did not found News object, creating news..");
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastDate = currentDate.minusDays(1); // Subtract one day to get the last date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String lastDay = lastDate.format(formatter);


        News obj_news = new News(cityName);
        obj_news.setNewsList(this.fetchNewsStream(cityName,pageSize,lastDay ,obj_news.getNewsList()));
        this.saveNews(obj_news);
        logger.info("NewsService>>  getNews: End method.");
        return obj_news;
    }

    public List<News> getAllNews(){
        logger.info("NewsService>>  getAllNews: Start method.");
        return this.newsRepository.findAll();
    }

    public boolean deleteAllNews(){
        logger.info("NewsService>>  deleteAllNews: Start method.");
        this.newsRepository.deleteAll();
        return true;
    }

}
