package sg.edu.nus.iss.workshop27.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.repo.GameRepo;
import sg.edu.nus.iss.workshop27.service.GameService;
import static sg.edu.nus.iss.workshop27.Constants.*;

@RestController
public class GameRestController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepo gameRepo;

    @GetMapping(path = "/games", produces = MediaType.APPLICATION_JSON_VALUE)
    public String browseGames(
            @RequestParam(defaultValue = "25") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return gameService.getListOfGames(limit, offset).toString();
    }

    @GetMapping(path = "/games/rank", produces = MediaType.APPLICATION_JSON_VALUE)
    public String browseGamesByRanking(
            @RequestParam(defaultValue = "25") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return gameService.getListOfGamesByRanking(limit, offset).toString();
    }

    @GetMapping(path = "/game/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSingleGame(@PathVariable String id) {
        JsonObject jsonObj = gameService.getGamesById(id);
        if (jsonObj != null) {
            return jsonObj.toString();
        }
        return "Not found";
    }

    @Validated
    @PostMapping(path = "/review", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.TEXT_PLAIN_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insertGameReview(@Valid Review review, BindingResult result,
            HttpServletResponse response, Model model) {

        if (!(gameRepo.checkGameId(review.getGid()))) {
            FieldError fieldErr = new FieldError("Review", FIELD_GAME_ID, "Game Id does not exists");
            result.addError(fieldErr);
        }

        if (result.hasErrors()) {
            List<FieldError> listOfErr = result.getFieldErrors();
            JsonObjectBuilder job = Json.createObjectBuilder();
            listOfErr.stream()
                    .forEach(x -> {
                        job.add(x.getField(), x.getDefaultMessage());
                    });

            return ResponseEntity.badRequest().body(job.build().toString());
        }

        Optional<Document> opt = gameService.getGameName(review.getGid());

        review.setPosted(new Date());
        String name = opt.get().getString("name");
        review.setName(name);

        Document doc = gameService.insertComment(review);
        doc.replace("posted", doc.getDate("posted").toString());
        doc.remove("_id");
        // doc.replace("_id", doc.getObjectId("_id").toString());

        return ResponseEntity
                .ok()
                .body(doc.toJson());

    }

    @PutMapping(path = "/review/{reviewId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateGameReview(@PathVariable String reviewId, @RequestBody @Valid Review review,
            BindingResult binding) {

        // validate rating field
        if (binding.hasFieldErrors("rating")) {
            JsonObjectBuilder jsonObjbldr = Json.createObjectBuilder()
                    .add("error", 406)
                    .add("rating", binding.getFieldError("rating").getDefaultMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(jsonObjbldr.build().toString());
        }

        // if reviewId is a valid hexstring and exists in database
        if (ObjectId.isValid(reviewId) && (gameRepo.doesCommentExists(reviewId))) {

            Document newReview = new Document();
            newReview.append("comment", review.getComment());
            newReview.append("rating", review.getRating());
            newReview.append("posted", new Date());

            gameService.updateComment(newReview, reviewId);
            return ResponseEntity
                    .ok()
                    .body("Success");
        }

        // else if reviewId is not valid hexstring and does not exist
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("Comment Id", reviewId)
                .add("message", "Not found")
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(jsonObj.toString());
    }

    @GetMapping(path = "/review/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameReview(@PathVariable String reviewId) {
        JsonObject jsonObj = gameService.getReview(reviewId);
        return ResponseEntity.ok().body(jsonObj.toString());
    }

    // reviewId is hexString => TODO: change to c_id
    @GetMapping(path = "/review/{reviewId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameHistory(@PathVariable String reviewId) {

        // if reviewId is a valid hexstring and exists in database
        if (ObjectId.isValid(reviewId) && (gameRepo.doesCommentExists(reviewId))) {
            JsonObject jsonObj = gameService.getHistory(reviewId);
            return ResponseEntity.ok().body(jsonObj.toString());
        }

        // else if reviewId is not valid hexstring and does not exist
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("Comment Id", reviewId)
                .add("message", "Not found")
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(jsonObj.toString());
    }

    // gameId => integer, not hexstring
    @GetMapping(path = "/game/{gameId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameWithReviews(@PathVariable int gameId) {
        if (gameService.checkGameId(gameId)) {
            return ResponseEntity.ok().body(gameService.getGameWithReviews(gameId).toString());
        }
        JsonObject jsonObj = Json.createObjectBuilder()
                .add(FIELD_GAME_ID, gameId)
                .add("message", "NOT FOUND")
                .build();
        return ResponseEntity.badRequest().body(jsonObj.toString());
    }

    @GetMapping(path = "/games/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameHighestOrLowestRatingToJson(@PathVariable String direction) {

        // List<String> directions = new ArrayList<String>(List.of("highest", "lowest"));
        direction = direction.toLowerCase();
        if (directions.contains(direction)) {
            return ResponseEntity.ok().body(gameService.getGameHighestRating(direction).toString());
        }
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("Error", 406)
                .add("message", "Invalid path: " + direction)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonObj.toString());

    }

}
