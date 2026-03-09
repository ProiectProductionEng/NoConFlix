package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.GenreEntity;

@Repository
public interface GenreRepository extends MongoRepository<GenreEntity, String> {

    //List<MovieEntity> findByMovieId(String movieId);
}
