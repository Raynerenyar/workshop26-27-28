package sg.edu.nus.iss.workshop27.service;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.JsonObject;
import sg.edu.nus.iss.workshop27.Util;
import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.repo.GameRepo;

import static sg.edu.nus.iss.workshop27.Constants.*;

@Service
public class GameService {
    @Autowired
    GameRepo gameRepo;

    public JsonObject getListOfGames(int limit, int offset) {
        List<Document> listOfDocs = gameRepo.listAllGames(limit, offset);
        return Util.getListOfGamesDocToJson(listOfDocs, gameRepo.countGames(), limit, offset);

    }

    public JsonObject getListOfGamesByRanking(int limit, int offset) {
        List<Document> listOfDocs = gameRepo.listGamesByRanking(limit, offset);
        return Util.getListOfGamesDocToJson(listOfDocs, gameRepo.countGames(), limit, offset);
    }

    public JsonObject getGamesById(String id) {
        Optional<Document> opt = gameRepo.getGameById(id);
        if (opt.isPresent()) {
            return Util.singleGameDocToJson(opt.get());
        }
        return null;
    }

    public Boolean checkGameId(Integer gid) {

        return gameRepo.checkGameId(gid);
    }

    public Optional<Document> getGameName(Integer gid) {

        return gameRepo.getGameName(gid);
    }

    public String doesCommentExists(Review review) {
        return gameRepo.doesCommentExists(review);
    }

    public Document insertComment(Review review) {

        return gameRepo.insertComment(review);
    }

    public void updateComment(Document newReview, String reviewId) {
        gameRepo.updateComment(newReview, reviewId);
    }

    public JsonObject getReview(String reviewId) {
        Document doc = gameRepo.getReview(reviewId);

        return Util.reviewToJson(doc);
    }

    public JsonObject getHistory(String reviewId) {
        Document doc = gameRepo.getHistoryReview(reviewId);
        return Util.historyToJson(doc);
    }

    public JsonObject getGameWithReviews(int gameId) {
        Document doc = gameRepo.getReviewsByGameId(gameId);
        return Util.gameReviewsToJson(doc);
    }

    public JsonObject getGameHighestRating(String direction) {
        Boolean dir = direction.equalsIgnoreCase(directions.get(0)) ? true : false;
        List<Document> listDocs = gameRepo.getGamesHighestRating(dir);
        return Util.gameHighestOrLowestRatingToJson(listDocs, direction);
    }

}
