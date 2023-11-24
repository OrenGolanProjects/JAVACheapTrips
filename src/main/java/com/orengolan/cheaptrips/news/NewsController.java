package com.orengolan.cheaptrips.news;


import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private static final Logger logger = Logger.getLogger(NewsController.class.getName());
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping("/get-specific-news")
    public News getSpecificNews(@RequestParam("cityName") String cityName) throws IOException {
        logger.info("** NewsController>>  getSpecificNews: Start method.");
        return this.newsService.getNews(cityName, 10);
    }

    @RequestMapping(value = "get-all-news",method = RequestMethod.GET)
    public List<News> getAllNews(){
        logger.info("** NewsController>>  getAllNews: Start method.");
        return this.newsService.getAllNews();
    }
    @RequestMapping(value = "delete-all-news",method = RequestMethod.DELETE)
    public Boolean deleteAllNews(){
        logger.info("** NewsController>>  deleteAllNews: Start method.");
        return this.newsService.deleteAllNews();
    }
}
