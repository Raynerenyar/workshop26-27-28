package sg.edu.nus.iss.workshop27.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.bson.types.ObjectId;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.Util;

import static sg.edu.nus.iss.workshop27.Constants.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public class GameRepo {

    @Autowired
    MongoTemplate template;

    /* 
    db.game.aggregate([
    {
        $project: {name:1, gid:1}    
    },
    {
        $sort: {gid:1}
    },
    {
        $skip: 0
    },
    {
        $limit:25
    }
    ])
    */
    public List<Document> listAllGames(int limit, int offset) {

        ProjectionOperation projectAggr = Aggregation.project(FIELD_GAME_ID, FIELD_GAME_NAME);
        SortOperation sortAggr = Aggregation.sort(Sort.by(Direction.ASC, FIELD_GAME_NAME));
        LimitOperation limitAggr = Aggregation.limit(limit);
        SkipOperation skipAggr = Aggregation.skip(offset);

        Aggregation pipeline = Aggregation.newAggregation(projectAggr, sortAggr, skipAggr, limitAggr);
        return template.aggregate(pipeline, MONGO_COLLECTION_NAME_GAME, Document.class).getMappedResults();
    }

    public long countGames() {
        return template.estimatedCount(MONGO_COLLECTION_NAME_GAME);
    }

    /* 
        db.game.aggregate([
        {
            $project: {name:1, gid:1, ranking:1}    
        },
        {
            $sort: {ranking:1}
        },
        {
            $skip:0
        },
        {
            $limit:25
        }
        ])
    */
    public List<Document> listGamesByRanking(int limit, int offset) {
        ProjectionOperation projectAggr = Aggregation.project(FIELD_OBJECT_ID, FIELD_GAME_NAME, FIELD_GAME_RANKING);
        SortOperation sortAggr = Aggregation.sort(Sort.by(Direction.ASC, FIELD_GAME_RANKING));
        LimitOperation limitAggr = Aggregation.limit(limit);
        SkipOperation skipAggr = Aggregation.skip(offset);

        Aggregation pipeline = Aggregation.newAggregation(projectAggr, sortAggr, skipAggr, limitAggr);
        return template.aggregate(pipeline, MONGO_COLLECTION_NAME_GAME, Document.class).getMappedResults();
    }

    /* 
        db.game.find(
            { _id: ObjectId("63eb8926a2b39ac37bb8a0fb") }
        )
    */
    public Optional<Document> getGameById(String id) {
        ObjectId objId = new ObjectId(id);
        Criteria criteria = Criteria.where(FIELD_OBJECT_ID).is(objId);

        Query query = new Query();
        query.addCriteria(criteria);

        if (template.exists(query, Document.class, MONGO_COLLECTION_NAME_GAME)) {
            return Optional.of(template.findOne(query, Document.class, MONGO_COLLECTION_NAME_GAME));
        }
        return Optional.empty();
    }

    public Boolean checkGameId(Integer gid) {

        Criteria criteria = new Criteria();
        criteria = Criteria.where(FIELD_GAME_ID).is(gid);

        Query query = new Query(criteria);

        return template.exists(query, Document.class, MONGO_COLLECTION_NAME_GAME);
    }

    /* 
       db.game.find({gid:2},{name:1})
     */
    public Optional<Document> getGameName(Integer gid) {
        Criteria criteria = new Criteria();
        criteria = Criteria.where(FIELD_GAME_ID).is(gid);

        Query query = new Query(criteria);
        query.fields().include(FIELD_GAME_NAME);

        Document doc = template.findOne(query, Document.class, MONGO_COLLECTION_NAME_GAME);
        if (doc != null) {
            return Optional.of(doc);
        }
        return Optional.empty();
    }

    /* 
       db.reviews.insert(
        <doc>
       )
     */
    public Document insertComment(Review review) {

        Document doc = Util.toDoc(review);
        System.out.println(doc.toString());
        Document newDoc = template.insert(doc, MONGO_COLLECTION_NAME_REVIEWS);
        System.out.println(newDoc.toString());
        return newDoc;
    }

    /* 
     * db.reviews.find({_id:ObjectId("63f5dc5fb3f1844301c54742")})
     */
    public Boolean doesCommentExists(String reviewId) {

        ObjectId objId = new ObjectId(reviewId);
        Criteria criteria = Criteria.where(FIELD_OBJECT_ID).is(objId);
        Query query = new Query(criteria);
        return template.exists(query, MONGO_COLLECTION_NAME_REVIEWS);
    }

    /* 
        db.reviews.updateOne(
            {_id:ObjectId(<String_object_id>)},
            {$set:{
                rating:<int_new_rating>
                comment:<String_new_comment>
            }},
            {
                upsert:false
            }
        )
     */
    public void updateComment(Document newReview, String reviewId) {

        // Document doc = Util.toDoc(review);
        ObjectId objId = new ObjectId(reviewId);
        Criteria criteria = Criteria.where(FIELD_OBJECT_ID).is(objId);
        Query query = new Query(criteria);
        Update update = new Update().push(REVIEWS_FIELD_EDITED, newReview);
        template.updateFirst(query, update, Document.class, MONGO_COLLECTION_NAME_REVIEWS);
    }

    // ***************** NOT IN USE *************
    /* 
        db.reviews.find({
            user:{$regex:"john", $options:"i"},
            ID:3,
            name:"Samurai"
        })
     */
    public String doesCommentExists(Review review) {

        List<Criteria> listOfCriterias = new LinkedList<>();
        listOfCriterias.add(Criteria.where(REVIEWS_FIELD_USER).regex(review.getUser(), "i"));
        listOfCriterias.add(Criteria.where(REVIEWS_FIELD_GAME_ID).is(review.getGid()));
        listOfCriterias.add(Criteria.where(FIELD_GAME_NAME).is(review.getName()));

        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(listOfCriterias.toArray(new Criteria[listOfCriterias.size()])));

        Document doc = template.findOne(query, Document.class, MONGO_COLLECTION_NAME_REVIEWS);
        if (doc != null) {
            return doc.getObjectId(FIELD_OBJECT_ID).toString();
        }
        return null;
    }

    /* 
        db.reviews.aggregate([
            {$match:
                {_id:ObjectId("63f766fe42e24b24749c7286")}
            },
            {
                $unwind:"$edited"
            },
            {
                $sort:{"edited":-1}
            },
            {
                $limit:1
            }
        ])
     */
    public Document getReview(String reviewId) {

        // all pipeline operations
        MatchOperation match = Aggregation.match(Criteria.where(FIELD_OBJECT_ID).is(new ObjectId(reviewId)));
        UnwindOperation unwind = Aggregation.unwind(REVIEWS_FIELD_EDITED);
        SortOperation sort = Aggregation.sort(Sort.by(Direction.DESC, REVIEWS_FIELD_EDITED));
        LimitOperation limit = Aggregation.limit(1);
        CountOperation count = Aggregation.count().as(REVIEWS_FIELD_EDITED);

        // get count of edited reviews
        Aggregation pipeline2 = Aggregation.newAggregation(match, unwind, sort, count);
        Document forCount = template.aggregate(pipeline2, MONGO_COLLECTION_NAME_REVIEWS, Document.class)
                .getUniqueMappedResult();

        // if there are edited reviews
        if (forCount != null && forCount.getInteger(REVIEWS_FIELD_EDITED) >= 1) {
            Aggregation pipeline = Aggregation.newAggregation(match, unwind, sort, limit);
            Document doc = template.aggregate(pipeline, MONGO_COLLECTION_NAME_REVIEWS, Document.class)
                    .getUniqueMappedResult();
            Document editedDoc = (Document) doc.get(REVIEWS_FIELD_EDITED);

            doc.append(REVIEWS_FIELD_COMMENT, editedDoc.getString(REVIEWS_FIELD_COMMENT))
                    .append(REVIEWS_FIELD_POSTED, doc.getDate(REVIEWS_FIELD_POSTED))
                    .append(REVIEWS_FIELD_RATING, doc.getInteger(REVIEWS_FIELD_RATING));
            return doc.append(REVIEWS_FIELD_EDITED, true).append(REVIEWS_FIELD_TIMESTAMP, new Date());
        }

        // if there are no edited reviews
        Aggregation pipeline = Aggregation.newAggregation(match);
        Document doc = template.aggregate(pipeline, MONGO_COLLECTION_NAME_COMMENT, Document.class)
                .getUniqueMappedResult();

        return doc.append(REVIEWS_FIELD_EDITED, false).append(REVIEWS_FIELD_TIMESTAMP, new Date());
    }

    /* 
        db.reviews.find(
            {
                _id:ObjectId("63f766fe42e24b24749c7286")
            },
            {
                _id:0
            }
        )
     */
    public Document getHistoryReview(String reviewId) {
        Criteria criteria = Criteria.where(FIELD_OBJECT_ID).is(new ObjectId(reviewId));
        Query query = Query.query(criteria);
        query.fields().exclude(FIELD_OBJECT_ID);

        return template.findOne(query, Document.class, MONGO_COLLECTION_NAME_REVIEWS);
    }

    /* 
        db.game.find({gid:3})
    
        db.comment.aggregate([
            {
                $match:{gid:3}
            },
            {
                $project:{_id:0, c_id:1}
            }
        ])
    
        db.game.aggregate([
            {
                $match:{gid:2}
            },
            {
                $lookup:{
                    from:"reviews", foreignField:"gid", localField:"gid", as:"reviews"
                }
            }
        ])
     */
    public Document getReviewsByGameId(int gameId) {

        // MAYBE BETTER TO DO SEPARATE MONGO QUERY THAN TO DO MONGO-AGGREGATE-LOOKUP

        // Criteria criteria = Criteria.where(FIELD_GAME_ID).is(gameId);
        // MatchOperation match = Aggregation.match(criteria);
        // ProjectionOperation project = Aggregation.project(COMMENT_FIELD_COMMENT_ID).andExclude(FIELD_OBJECT_ID);

        // Query query = new Query();
        // query.addCriteria(criteria);
        // query.fields().exclude(FIELD_OBJECT_ID);
        // Document doc = template.findOne(query, Document.class, MONGO_COLLECTION_NAME_GAME);

        // Aggregation pipeline = Aggregation.newAggregation(match, project);
        // List<Document> listDocs = template.aggregate(pipeline, MONGO_COLLECTION_NAME_COMMENT, Document.class)
        //         .getMappedResults();
        // return doc.append("reviews", listDocs);

        Criteria criteria = Criteria.where(FIELD_GAME_ID).is(gameId);

        MatchOperation match = Aggregation.match(criteria);

        LookupOperation lookup = Aggregation.lookup(MONGO_COLLECTION_NAME_REVIEWS, FIELD_GAME_ID, REVIEWS_FIELD_GAME_ID,
                "reviews");

        Aggregation pipeline = Aggregation.newAggregation(match, lookup);

        return template.aggregate(pipeline, MONGO_COLLECTION_NAME_GAME, Document.class).getUniqueMappedResult();
    }

    /* 
        db.comment.aggregate([
            {
                $sort:{"gid":1,"rating":1,"posted":-1}
            },
            {
                $group:{
                    _id:"$gid",
                    maxRating:{$max:"$rating"},
                    comment:{$first:"$$ROOT"}
                }
            },
            {
                $lookup:{from:"game",foreignField:"gid",localField:"_id",as:"game"}  
            },
            {
                $sort:{"_id":1}
            },
            {
                $unwind:"$game"
            },
            {
                $project:{"_id":"$comment.gid",name:"$game.name",rating:"$comment.rating",user:"$comment.user",comment:"$comment.c_text",review_id:"$comment.c_id"}
            },
            {
                $limit:3
            }
        ])
     */
    public List<Document> getGamesHighestRating(Boolean sortDirection) {

        // true for highest, false for lowest
        Direction ratingDirection = (sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;

        SortOperation sort = Aggregation
                .sort(Sort.Direction.ASC, REVIEWS_FIELD_GAME_ID)
                .and(ratingDirection, REVIEWS_FIELD_RATING)
                .and(Sort.Direction.DESC, REVIEWS_FIELD_POSTED);

        GroupOperation group = Aggregation.group(REVIEWS_FIELD_GAME_ID)
                .and(REVIEWS_FIELD_COMMENT,
                        AggregationExpression.from(MongoExpression.create("""
                                $first:"$$ROOT"
                                    """)));

        LookupOperation lookup = LookupOperation.newLookup().from(MONGO_COLLECTION_NAME_GAME)
                .localField(COMMENT_FIELD_OBJECT_ID)
                .foreignField(FIELD_GAME_ID)
                .as("game");

        UnwindOperation unwind = Aggregation.unwind(MONGO_COLLECTION_NAME_GAME);

        ProjectionOperation project = Aggregation.project()
                .andExpression(MONGO_COLLECTION_NAME_COMMENT + "." + COMMENT_FIELD_GAME_ID).as("_id")
                .andExpression(MONGO_COLLECTION_NAME_GAME + "." + FIELD_GAME_NAME).as("name")
                .andExpression(MONGO_COLLECTION_NAME_COMMENT + "." + COMMENT_FIELD_RATING).as("rating")
                .andExpression(MONGO_COLLECTION_NAME_COMMENT + "." + COMMENT_FIELD_USER).as("user")
                .andExpression(MONGO_COLLECTION_NAME_COMMENT + "." + COMMENT_FIELD_COMMENT).as("comment")
                .andExpression(MONGO_COLLECTION_NAME_COMMENT + "." + COMMENT_FIELD_COMMENT_ID).as("review_id");

        LimitOperation limit = Aggregation.limit(3);

        Aggregation pipeline = Aggregation.newAggregation(sort, group, lookup, unwind, project, limit);
        return template.aggregate(pipeline, MONGO_COLLECTION_NAME_COMMENT, Document.class)
                .getMappedResults();
    }
}
