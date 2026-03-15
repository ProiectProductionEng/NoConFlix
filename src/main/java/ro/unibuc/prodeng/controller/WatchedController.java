package ro.unibuc.prodeng.controller;

import org.springframework.web.bind.annotation.*;
import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.service.WatchedService;

import java.util.List;

@RestController
@RequestMapping("/api/watched")
public class WatchedController {

    private final WatchedService watchedService;

    public WatchedController(WatchedService watchedService) {
        this.watchedService = watchedService;
    }

    @PostMapping("/progress")
    public Watched updateProgress(
            @RequestParam String userId,
            @RequestParam String movieId,
            @RequestParam Integer lastTime) {

        return watchedService.updateProgress(userId, movieId, lastTime);
    }

    @GetMapping("/{userId}")
    public List<Watched> getAllWatchedForUser(@PathVariable String userId) {
        return watchedService.getAllWatchedForUser(userId);
    }

    @GetMapping("/continue/{userId}")
    public List<Watched> getContinueWatching(@PathVariable String userId) {
        return watchedService.getContinueWatching(userId);
    }
}