package com.orengolan.cheaptrips.news;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@code News} class represents news information associated with a specific city.
 * It is a MongoDB document mapped to the "news" collection, containing details such as
 * the city name, a list of news articles, and an expiration timestamp for one day.
 *
 * Key Features:
 * - {@code @Document}: Indicates that instances of this class should be stored in the "news" collection.
 * - {@code @Indexed(expireAfterSeconds)}: Configures an index for automatic document expiration after 24 hours.
 * - Nested {@code news} class: Represents individual news articles with various attributes such as source,
 *   author, title, description, URL, image URL, publication date, content, and sentiment analyzer score.
 *
 * Example Usage:
 * The class is utilized to store and retrieve news information for different cities. Each instance of
 * the class corresponds to a city and contains a list of news articles with associated details. The
 * expiration timestamp ensures that news data is automatically removed after 24 hours.
 *
 * Note: Ensure that MongoDB is properly configured, and the required dependencies are added to use
 * Spring Data MongoDB annotations and features effectively.
 */
@Document(collection = "news")
public class News {

    @Id
    private String _id;

    @Indexed(expireAfterSeconds = 24 * 60 * 60) // One day expiration
    private Date expireAt;
    @NotNull
    @Indexed(unique = true)
    @Size(max = 50)
    private final String cityName;
    private List<news> newsList;
    private Integer _newsListCount;


    /**
     * Nested class representing individual news articles.
     */
    public static class news{
        private final String source_name;
        private final String author;
        private final String title;
        private final String description;
        private final String url;
        private final String urlToImage;
        private final String publishedAt;
        private final String content;
        private final Double scoreSentimentAnalyzer;

        public news(String source_name, String author, String title, String description, String url, String urlToImage, String publishedAt, String content, Double scoreSentimentAnalyzer) {
            this.source_name = source_name;
            this.author = author;
            this.title = title;
            this.description = description;
            this.url = url;
            this.urlToImage = urlToImage;
            this.publishedAt = publishedAt;
            this.content = content;
            this.scoreSentimentAnalyzer = scoreSentimentAnalyzer;
        }

        public String getSource_name() {
            return source_name;
        }

        public String getAuthor() {
            return author;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public String getContent() {
            return content;
        }

        public Double getScoreSentimentAnalyzer() {
            return scoreSentimentAnalyzer;
        }
    }


    public News(String cityName) {
        this.cityName = cityName;
        this.newsList = new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public Date getExpireAt() {
        return this.expireAt;
    }

    public String getCityName() {
        return cityName;
    }

    public List<news> getNewsList() {
        return newsList;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public void setNewsList(List<news> newsList) {
        this.newsList = newsList;
    }

    public Integer getNewsListCount() {
        return this._newsListCount;
    }

    public void setNewsListCount(Integer newsListCount) {
        this._newsListCount = newsListCount;
    }


    @Override
    public String toString() {
        return "News={" +
                "_id='" + this.get_id() + '\'' +
                ", expireAt=" + this.getExpireAt() +
                ", cityName='" + this.getCityName() + '\'' +
                ", newsList=" + this.getNewsList() +
                '}';
    }
}
