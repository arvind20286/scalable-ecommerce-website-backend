package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.Review;
import com.product_service_api.Repository.ProductRepository;
import com.product_service_api.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    public Review addProductReview(ReviewRequestDTO reviewRequestDTO) {
        Long userId = authServiceClient.getUserIdJWT().getUserId();
        if (!productRepository.existsById(reviewRequestDTO.getProductId()) || reviewRepository.existsByUserIdAndProductId(userId, reviewRequestDTO.getProductId())) {
            throw new RuntimeException("Either product does not exist or review already exists");
        }
        Product product = productRepository.getReferenceById(reviewRequestDTO.getProductId());
        Review review = new Review(null, userId, reviewRequestDTO.getRating(), reviewRequestDTO.getComment(), product, null);
        reviewRepository.save(review);
        return review;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();

    }
}
