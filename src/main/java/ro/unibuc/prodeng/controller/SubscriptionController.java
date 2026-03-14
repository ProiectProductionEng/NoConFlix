package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import ro.unibuc.prodeng.request.ChangeNameRequest;
import ro.unibuc.prodeng.request.EditSubscriptionRequest;
import ro.unibuc.prodeng.request.EditSubscriptionRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.request.CreateSubscriptionRequest;
import ro.unibuc.prodeng.request.CreateSubscriptionRequest;
import ro.unibuc.prodeng.response.UserResponse;
import ro.unibuc.prodeng.response.SubscriptionResponse;
import ro.unibuc.prodeng.response.SubscriptionResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() throws EntityNotFoundException {
        List<SubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @PathVariable String id,
            @Valid @RequestBody EditSubscriptionRequest request) throws EntityNotFoundException {
        SubscriptionResponse subscription = subscriptionService.editSubscription(id, request);
        return ResponseEntity.ok(subscription);
    }

    @PatchMapping("/subscribe/{uid}")
    public ResponseEntity<SubscriptionResponse> subscribeUser(
            @PathVariable @NotBlank String uid,
            @Valid @RequestBody EditSubscriptionRequest request) throws EntityNotFoundException {
        SubscriptionResponse subscription = subscriptionService.subscribeUser(uid, request);
        return ResponseEntity.ok(subscription);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String id) throws EntityNotFoundException {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

}
