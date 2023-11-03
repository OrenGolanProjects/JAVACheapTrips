package com.orengolan.CheapTrips.news;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
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
    public ResponseEntity<?> getSpecificNews(@RequestParam("cityName") String cityName) throws IOException {
        logger.info("** NewsController>>  getSpecificNews: Start method.");
        return ResponseEntity.ok(this.newsService.getNews(cityName, 10));
    }

    @RequestMapping(value = "get-all-news",method = RequestMethod.GET)
    public ResponseEntity<?> getAllNews(){
        logger.info("** NewsController>>  getAllNews: Start method.");
        return ResponseEntity.ok(this.newsService.getAllNews());
    }
    @RequestMapping(value = "delete-all-news",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllNews(){
        logger.info("** NewsController>>  deleteAllNews: Start method.");
        return ResponseEntity.ok(this.newsService.deleteAllNews());
    }
}
