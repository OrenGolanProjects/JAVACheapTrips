package com.orengolan.cheaptrips.news;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code NewsController} class is a Spring REST controller responsible for handling HTTP requests
 * related to news operations. It exposes endpoints for retrieving specific news based on a city name,
 * getting all available news, and deleting all news entries.
 *
 * Key Features:
 * - {@code @RestController}: Indicates that this class defines a REST controller.
 * - {@code @RequestMapping("/api/news")}: Specifies the base URL path for the controller.
 * - {@code @Api(tags)}: Provides Swagger documentation for the controller, categorizing it under "Admin Maintenance."
 * - Endpoints:
 *   - {@code /get-specific-news}: Retrieves specific news based on the provided city name.
 *   - {@code /get-all-news}: Retrieves all available news entries.
 *   - {@code /delete-all-news}: Deletes all news entries.
 *
 * Example Usage:
 * The class is used to expose news-related functionalities through HTTP endpoints. It delegates requests
 * to the {@code NewsService} for processing and retrieval of news data. The Swagger documentation enhances
 * API readability and provides information about the operations supported by the controller.
 *
 * Note: Ensure proper configuration of Spring MVC and Swagger annotations for effective documentation.
 */
@RestController
@RequestMapping("/api/news")
@Api(tags = "Admin Maintenance", description = "Admin Oversight for Essential Services.")
public class NewsController {
    private static final Logger logger = Logger.getLogger(NewsController.class.getName());
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Retrieves specific news based on the provided city name.
     *
     * @param cityName The name of the city for which news is requested.
     * @return The {@code News} object containing news details.
     * @throws IOException If an I/O error occurs during news retrieval.
     */
    @ApiOperation(value = "Get specific news", notes = "Retrieve specific news based on the provided city name.")
    @PostMapping("/get-specific-news")
    public News getSpecificNews(@RequestParam("cityName") String cityName) throws IOException {
        logger.info("** NewsController>>  getSpecificNews: Start method.");
        return this.newsService.getNews(cityName, 10);
    }

    /**
     * Retrieves all available news entries.
     *
     * @return A list of {@code News} objects representing all available news.
     */
    @ApiOperation(value = "Get all news", notes = "Retrieve all available news.")
    @RequestMapping(value = "get-all-news", method = RequestMethod.GET)
    public List<News> getAllNews(){
        logger.info("** NewsController>>  getAllNews: Start method.");
        return this.newsService.getAllNews();
    }
    /**
     * Deletes all available news entries.
     *
     * @return {@code true} if deletion is successful; {@code false} otherwise.
     */
    @ApiOperation(value = "Delete all news", notes = "Delete all available news.")
    @RequestMapping(value = "delete-all-news", method = RequestMethod.DELETE)
    public Boolean deleteAllNews(){
        logger.info("** NewsController>>  deleteAllNews: Start method.");
        return this.newsService.deleteAllNews();
    }
}
