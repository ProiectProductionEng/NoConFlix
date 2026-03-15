package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.prodeng.request.ChangeNameRequest;
import ro.unibuc.prodeng.request.EditAvailabilityRequest;
import ro.unibuc.prodeng.request.EditAvailabilityRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.request.CreateAvailabilityRequest;
import ro.unibuc.prodeng.request.CreateAvailabilityRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.response.AvailabilityResponse;
import ro.unibuc.prodeng.response.AvailabilityResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.service.AvailabilityService;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getAllAvailabilitys() throws EntityNotFoundException {
        List<AvailabilityResponse> availabilitys = availabilityService.getAllAvailabilities();
        return ResponseEntity.ok(availabilitys);
    }

    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(@RequestBody CreateAvailabilityRequest request) {
        AvailabilityResponse availability = availabilityService.createAvailability(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(availability);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody EditAvailabilityRequest request) throws EntityNotFoundException {
        AvailabilityResponse availability = availabilityService.editAvailability(id, request);
        return ResponseEntity.ok(availability);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable String id) throws EntityNotFoundException {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
