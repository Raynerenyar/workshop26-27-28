package sg.edu.nus.iss.workshop27;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String MONGO_DATABASE_NAME = "bgg";
    public static final String MONGO_COLLECTION_NAME_GAME = "game";
    public static final String MONGO_COLLECTION_NAME_COMMENT = "comment";
    public static final String MONGO_COLLECTION_NAME_REVIEWS = "reviews";

    public static final String FIELD_OBJECT_ID = "_id";

    public static final String FIELD_GAME_ID = "gid";
    public static final String FIELD_GAME_NAME = "name";
    public static final String FIELD_GAME_RANKING = "ranking";
    public static final String FIELD_GAME_USERS_RATED = "users_rated";
    public static final String FIELD_GAME_URL = "url";
    public static final String FIELD_GAME_IMAGE = "image";
    public static final String FIELD_GAME_YEAR = "year";

    public static final String COMMENT_FIELD_OBJECT_ID = "_id";
    public static final String COMMENT_FIELD_COMMENT_ID = "c_id";
    public static final String COMMENT_FIELD_GAME_ID = "gid";
    public static final String COMMENT_FIELD_USER = "user";
    public static final String COMMENT_FIELD_COMMENT = "c_text";
    public static final String COMMENT_FIELD_RATING = "rating";

    public static final Object REVIEWS_FIELD_OBJECT_ID = "_id";
    public static final String REVIEWS_FIELD_USER = "user";
    public static final String REVIEWS_FIELD_RATING = "rating";
    public static final String REVIEWS_FIELD_COMMENT = "comment";
    public static final String REVIEWS_FIELD_GAME_ID = "gid";
    public static final String REVIEWS_FIELD_POSTED = "posted";
    public static final String REVIEWS_FIELD_NAME = "name";
    public static final String REVIEWS_FIELD_EDITED = "edited";
    public static final String REVIEWS_FIELD_TIMESTAMP = "timestamp";

    private static final String DIRECTION1 = "highest";
    private static final String DIRECTION2 = "lowest";

    public static final List<String> directions = new ArrayList<String>(List.of(DIRECTION1, DIRECTION2));

}
