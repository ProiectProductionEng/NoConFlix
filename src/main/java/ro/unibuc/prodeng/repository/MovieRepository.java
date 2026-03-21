package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.MovieEntity;

@Repository
public interface MovieRepository extends MongoRepository<MovieEntity, String> {

    //List<MovieEntity> findByMovieId(String movieId);

    List<MovieEntity> findAllByOrderByTotalViewsDesc();

    List<MovieEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    @Query("{ $text: { $search: ?0 } }")
    List<MovieEntity> searchByText(String text);
}
