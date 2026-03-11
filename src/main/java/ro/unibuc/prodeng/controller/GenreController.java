package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.prodeng.request.ChangeNameRequest;
import ro.unibuc.prodeng.request.EditGenreRequest;
import ro.unibuc.prodeng.request.EditGenreRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.request.CreateGenreRequest;
import ro.unibuc.prodeng.request.CreateGenreRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.response.GenreResponse;
import ro.unibuc.prodeng.response.GenreResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.service.GenreService;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllGenres() throws EntityNotFoundException {
        List<GenreResponse> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(@RequestBody CreateGenreRequest request) {
        GenreResponse genre = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable String id,
            @Valid @RequestBody EditGenreRequest request) throws EntityNotFoundException {
        GenreResponse genre = genreService.editGenre(id, request);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable String id) throws EntityNotFoundException {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
