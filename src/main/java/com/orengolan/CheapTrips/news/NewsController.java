package com.orengolan.CheapTrips.news;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Size;
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

    @RequestMapping(value = "get-specific-news",method = RequestMethod.GET)
    public ResponseEntity<?> getSpecificNews(
                                        @Size(max = 2) @RequestParam(defaultValue = "Israel") String cityName,
                                       @Size(max = 3) @RequestParam(defaultValue = "10") Integer pageSize
                                       ) throws IOException {
        logger.info("NewsController>>fetchNews: Start method.");
        return ResponseEntity.ok(this.newsService.getNews(cityName, pageSize));
    }

    @RequestMapping(value = "get-all-news",method = RequestMethod.GET)
    public ResponseEntity<?> getAllNews(){
        return ResponseEntity.ok(this.newsService.getAllNews());
    }
    @RequestMapping(value = "delete-all-news",method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllNews(){
        return ResponseEntity.ok(this.newsService.deleteAllNews());
    }
}
