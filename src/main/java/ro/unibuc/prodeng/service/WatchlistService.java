package ro.unibuc.prodeng.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.model.Watchlist;
import ro.unibuc.prodeng.repository.WatchlistRepository;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public Watchlist addToWatchlist(String userId, String movieId) {

        if (watchlistRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new IllegalArgumentException("Movie already exists in watchlist");
        }

        Watchlist watchlist = new Watchlist(userId, movieId);

        try {
            return watchlistRepository.save(watchlist);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Movie already exists in watchlist");
        }
    }

    public List<Watchlist> getUserWatchlist(String userId) {
        return watchlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void removeFromWatchlist(String userId, String movieId) {
        watchlistRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
}