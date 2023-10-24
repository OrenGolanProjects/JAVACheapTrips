package com.orengolan.CheapTrips.news;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.CheapTrips.Service.SentimentAnalyzer;
import com.orengolan.CheapTrips.util.API;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.Date;


@Service
public class NewsService {

    private final Logger logger = Logger.getLogger(NewsService.class.getName());

    private final SentimentAnalyzer sentimentAnalyzer;
    private final ObjectMapper objectMapper;
    private final NewsRepository newsRepository;
    private final API api;

    public NewsService(SentimentAnalyzer sentimentAnalyzer, ObjectMapper objectMapper,NewsRepository newsRepository,API api) {
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.objectMapper = objectMapper;
        this.newsRepository = newsRepository;
        this.api = api;
    }

    private String getNewsStream(String cityName, Integer pageSize,String lastDay)  {
        String TOKEN = "8358e5edeb074b0784b6f128db5ff9c3";

        logger.info("NewsService>>getNewsStream: Start method.");
        logger.info("NewsService>>getNewsStream: Data: cityName:"+cityName+" , pageSize: "+pageSize+" ,lastDay:"+lastDay);

        String API_URL = "https://newsapi.org/v2/everything?q="+cityName+"&from="+lastDay+"&sortBy=popularity&apiKey="+ TOKEN +"&pageSize="+pageSize+"&language=en";
        return this.api.buildAndExecuteRequest(API_URL,null);
    }

    private List<News.news> analyzeNewsStream(String cityName, Integer pageSize,String lastDay,List<News.news> newsList) throws IOException {

        logger.info("NewsService>>analyzeNewsStream: Start method.");

        String json = getNewsStream(cityName,pageSize,lastDay);
        logger.info("NewsService>>analyzeNewsStream: Found "+json.length()+" news.");

        JsonNode rootNode = this.objectMapper.readTree(json);
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
        logger.info("NewsService>>analyzeNewsStream: End method.");
        return newsList;
    }

    public void saveNews(News news){
        news.setExpireAt(new Date(System.currentTimeMillis() + 604800000));
        news.setNewsListCount(news.getNewsList().size());
        this.newsRepository.insert(news);
    }

    public News getNews(String cityName, Integer pageSize) throws IOException {
        logger.info("NewsService>>getNews: Data: cityName:"+cityName+" , pageSize: "+pageSize);

        News existingNews = this.newsRepository.findByCityName(cityName);
        if(existingNews != null){
            logger.info("NewsService>>saveNews: Found News object.");
            return existingNews;
        }
        logger.info("NewsService>>getNews: Did not found News object, creating new document..");
        // Get news and fill the list with news objects.
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastDate = currentDate.minusDays(1); // Subtract one day to get the last date

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String lastDay = lastDate.format(formatter);

        News obj_news = new News(lastDay,cityName);
        logger.info("NewsController>>fetchNews: Start initialize news list.");

        obj_news.setNewsList(this.analyzeNewsStream("Travel to "+cityName,pageSize,lastDay ,obj_news.getNewsList()));
        logger.info("NewsController>>fetchNews: End initialize news list.");
        this.saveNews(obj_news);

        return obj_news;
    }

    public List<News> getAllNews(){
        return this.newsRepository.findAll();
    }

    public boolean deleteAllNews(){
        this.newsRepository.deleteAll();
        return true;
    }

}
