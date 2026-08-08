package com.product_service_api.Controller;

import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> addProductReview(@RequestBody ReviewRequestDTO reviewRequestDTO) {
        return new ResponseEntity<>(reviewService.addProductReview(reviewRequestDTO), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        return new ResponseEntity<>(reviewService.getAllReviews(), HttpStatus.OK);

    }
}
