package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.repository.WatchedRepository;
import ro.unibuc.prodeng.repository.MovieRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class WatchedService {

    private final WatchedRepository watchedRepository;
    private final MovieRepository movieRepository;

    public WatchedService(WatchedRepository watchedRepository, MovieRepository movieRepository) {
        this.watchedRepository = watchedRepository;
        this.movieRepository = movieRepository;
    }

    public Watched updateProgress(String userId, String movieId, Integer lastTime) {
        Optional<Watched> existingWatched = watchedRepository.findByUserIdAndMovieId(userId, movieId);

        if (existingWatched.isPresent()) {
            Watched watched = existingWatched.get();
            watched.setLastTime(lastTime);
            watched.setUpdatedAt(Instant.now());
            return watchedRepository.save(watched);
        }

        Watched watched = new Watched(userId, movieId, lastTime);
        return watchedRepository.save(watched);
    }

    public List<Watched> getContinueWatching(String userId) {

    List<Watched> watchedList = watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId);

    return watchedList.stream()
            .filter(watched -> {
                Optional<ro.unibuc.prodeng.model.MovieEntity> movieOpt =
                        movieRepository.findById(watched.getMovieId());

                if (movieOpt.isEmpty()) {
                    return false;
                }

                var movie = movieOpt.get();
                return watched.getLastTime() < movie.duration();
            })
            .toList();
    }
}