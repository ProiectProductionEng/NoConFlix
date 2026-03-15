package ro.unibuc.prodeng.controller;

import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.model.Watchlist;
import ro.unibuc.prodeng.service.WatchlistService;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @PostMapping
    public Watchlist addToWatchlist(@RequestParam String userId, @RequestParam String movieId) {
        return watchlistService.addToWatchlist(userId, movieId);
    }

    @GetMapping("/{userId}")
    public List<Watchlist> getUserWatchlist(@PathVariable String userId) {
        return watchlistService.getUserWatchlist(userId);
    }

    @DeleteMapping
    public void removeFromWatchlist(@RequestParam String userId, @RequestParam String movieId) {
        watchlistService.removeFromWatchlist(userId, movieId);
    }
}