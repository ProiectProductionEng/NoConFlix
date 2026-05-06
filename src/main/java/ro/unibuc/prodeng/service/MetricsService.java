package ro.unibuc.prodeng.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

    private final Counter moviesAddedCounter;
    private final Counter errorsCounter;
    private final Timer requestTimer;

    private final AtomicInteger moviesGauge;
    private final AtomicInteger activeUsersGauge;

    public MetricsService(MeterRegistry registry) {
        this.moviesAddedCounter = registry.counter("app_movies_added_total");
        this.errorsCounter = registry.counter("app_errors_total");
        this.requestTimer = registry.timer("app_request_duration_seconds");

        // GAUGES
        this.moviesGauge = registry.gauge("app_movies", new AtomicInteger(0));
        this.activeUsersGauge = registry.gauge("app_active_users", new AtomicInteger(0));
    }

    public void incrementMoviesAdded() {
        moviesAddedCounter.increment();
    }

    public void incrementErrors() {
        errorsCounter.increment();
    }

    public Timer getRequestTimer() {
        return requestTimer;
    }


    public void setTotalMovies(int count) {
        moviesGauge.set(count);
    }

    public void setMoviesCount(int count) { // dacă vrei să păstrezi și varianta veche
        moviesGauge.set(count);
    }

    public void setActiveUsers(int count) {
        activeUsersGauge.set(count);
    }
}