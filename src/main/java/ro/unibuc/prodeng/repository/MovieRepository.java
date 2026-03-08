package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.MovieEntity;

@Repository
public interface MovieRepository extends MongoRepository<MovieEntity, String> {

    //List<MovieEntity> findByAssignedMovieId(String assignedMovieId);
}
