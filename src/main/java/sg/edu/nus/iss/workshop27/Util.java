package sg.edu.nus.iss.workshop27;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.workshop27.model.Game;
import sg.edu.nus.iss.workshop27.model.Review;
import static sg.edu.nus.iss.workshop27.Constants.*;

import java.util.Date;
import java.util.List;

public class Util {

    public static JsonArrayBuilder getNameJsonArray(List<Document> doc) {
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        doc.stream().forEach(x -> {
            JsonObjectBuilder job = Json.createObjectBuilder()
                    .add("game_id", x.getObjectId(FIELD_OBJECT_ID).toString())
                    .add(FIELD_GAME_NAME, x.getString(FIELD_GAME_NAME));
            jsonArr.add(job);
        });
        return jsonArr;
    }

    public static JsonObject getListOfGamesDocToJson(List<Document> doc, long count, int limit, int offset) {
        return Json.createObjectBuilder()
                .add("games", getNameJsonArray(doc))
                .add("offset", offset)
                .add("limit", limit)
                .add("total", count)
                .add("timestamp", new Date().toString())
                .build();
    }

    public static JsonObject singleGameDocToJson(Document doc) {

        return Json.createObjectBuilder()
                .add("game_id", doc.getObjectId(FIELD_OBJECT_ID).toString())
                .add(FIELD_GAME_NAME, doc.getString(FIELD_GAME_NAME))
                .add(FIELD_GAME_YEAR, doc.getInteger(FIELD_GAME_YEAR))
                .add(FIELD_GAME_RANKING, doc.getInteger(FIELD_GAME_RANKING))
                .add(FIELD_GAME_USERS_RATED, doc.getInteger(FIELD_GAME_USERS_RATED))
                .add(FIELD_GAME_URL, doc.getString(FIELD_GAME_URL))
                .add("thumbnail", doc.getString(FIELD_GAME_IMAGE))
                .add("timestamp", new Date().toString())
                .build();
    }

    public static Document toDoc(Review review) {

        return new Document()
                .append(REVIEWS_FIELD_USER, review.getUser())
                .append(REVIEWS_FIELD_RATING, review.getRating())
                .append(REVIEWS_FIELD_COMMENT, review.getComment())
                .append(REVIEWS_FIELD_GAME_ID, review.getGid())
                .append(REVIEWS_FIELD_POSTED, review.getPosted())
                .append(REVIEWS_FIELD_NAME, review.getName());
    }

    public static JsonObject reviewToJson(Document doc) {
        return Json.createObjectBuilder()
                .add(REVIEWS_FIELD_USER, doc.getString(REVIEWS_FIELD_USER))
                .add(REVIEWS_FIELD_RATING, doc.getInteger(REVIEWS_FIELD_RATING))
                .add(REVIEWS_FIELD_COMMENT, doc.getString(REVIEWS_FIELD_COMMENT))
                .add(REVIEWS_FIELD_GAME_ID, doc.getInteger(REVIEWS_FIELD_GAME_ID))
                .add(REVIEWS_FIELD_POSTED, doc.getDate(REVIEWS_FIELD_POSTED).toString())
                .add(REVIEWS_FIELD_NAME, doc.getString(REVIEWS_FIELD_NAME))
                .add(REVIEWS_FIELD_EDITED, doc.getBoolean(REVIEWS_FIELD_EDITED))
                .add(REVIEWS_FIELD_TIMESTAMP, doc.getDate(REVIEWS_FIELD_TIMESTAMP).toString())
                .build();
    }

    public static JsonObject historyToJson(Document doc) {

        List<Document> list = doc.getList(REVIEWS_FIELD_EDITED, Document.class);
        List<JsonObject> listString = list.stream()
                .map(x -> {
                    return Json.createObjectBuilder()
                            .add(REVIEWS_FIELD_COMMENT, x.getString(REVIEWS_FIELD_COMMENT))
                            .add(REVIEWS_FIELD_RATING, x.getInteger(REVIEWS_FIELD_RATING))
                            .add(REVIEWS_FIELD_POSTED, x.getDate(REVIEWS_FIELD_POSTED).toString())
                            .build();
                }).toList();

        JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder(listString);

        return Json.createObjectBuilder()
                .add(REVIEWS_FIELD_USER, doc.getString(REVIEWS_FIELD_USER))
                .add(REVIEWS_FIELD_RATING, doc.getInteger(REVIEWS_FIELD_RATING))
                .add(REVIEWS_FIELD_COMMENT, doc.getString(REVIEWS_FIELD_COMMENT))
                .add(REVIEWS_FIELD_GAME_ID, doc.getInteger(REVIEWS_FIELD_GAME_ID))
                .add(REVIEWS_FIELD_POSTED, doc.getDate(REVIEWS_FIELD_POSTED).toString())
                .add(REVIEWS_FIELD_NAME, doc.getString(REVIEWS_FIELD_NAME))
                .add(REVIEWS_FIELD_EDITED, jsonArrBldr)
                .add(REVIEWS_FIELD_TIMESTAMP, new Date().toString())
                .build();
    }

    public static JsonObject gameReviewsToJson(Document doc) {
        JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder();

        doc.getList("reviews", Document.class, List.of())
                .stream()
                .forEach(x -> {
                    StringBuilder strBldr = new StringBuilder();
                    String uri = strBldr.append("/review/")
                            .append(x.getObjectId(REVIEWS_FIELD_OBJECT_ID))
                            .toString();
                    jsonArrBldr.add(uri);
                });

        // init JsonObjectBuilder
        JsonObjectBuilder jsonObjBldr = Json.createObjectBuilder();

        // map field types to the appropriate Document methods when getting values from Document
        List.of(Game.class.getDeclaredFields())
                .stream()
                .forEach(x -> {
                    String typeName = x.getType().getName();
                    String word = x.getName();
                    if (typeName.contains("String")) {
                        jsonObjBldr.add(word, doc.getString(word));
                    }
                    if (typeName.contains("int")) {
                        jsonObjBldr.add(word, doc.getInteger(word));
                    }
                });

        // add the last 2 objects
        jsonObjBldr.add("timestamp", new Date().toString());
        jsonObjBldr.add("reviews", jsonArrBldr);
        return jsonObjBldr.build();
    }

    public static JsonObject gameHighestOrLowestRatingToJson(List<Document> listDocs, String direction) {
        JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder();

        // converts each document to json then add to array
        listDocs.stream().forEach(x -> {
            JsonObjectBuilder jsonObjBldr = Json.createObjectBuilder()
                    .add("_id", x.getInteger(FIELD_OBJECT_ID))
                    .add("name", x.getString(REVIEWS_FIELD_NAME))
                    .add("rating", x.getInteger(REVIEWS_FIELD_RATING))
                    .add("user", x.getString(REVIEWS_FIELD_USER))
                    .add("comment", x.getString(REVIEWS_FIELD_COMMENT))
                    .add("review_id", x.getString("review_id"));
            jsonArrBldr.add(jsonObjBldr);
        });

        // String dir = (direction) ? "highest" : "lowest";
        return Json.createObjectBuilder()
                .add("rating", direction)
                .add("games", jsonArrBldr)
                .add("timestamp", new Date().toString())
                .build();
    }

}
