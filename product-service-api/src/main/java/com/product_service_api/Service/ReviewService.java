package com.product_service_api.Service;

import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.Entity.Review;

import java.util.List;

public interface ReviewService {
    Review addProductReview(ReviewRequestDTO reviewRequestDTO);
    List<Review> getAllReviews();
}
