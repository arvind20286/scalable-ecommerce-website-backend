package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.DTO.UserIdDTO;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.Review;
import com.product_service_api.Repository.ProductRepository;
import com.product_service_api.Repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewServiceImpl Unit Tests")
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
    }

    @Test
    @DisplayName("Should add product review successfully")
    void testAddProductReview_Success() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setProductId(1L);
        request.setRating(5);
        request.setComment("Great product!");

        UserIdDTO userIdDTO = new UserIdDTO();
        userIdDTO.setUserId(1L);

        when(authServiceClient.getUserIdJWT()).thenReturn(userIdDTO);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(productRepository.getReferenceById(1L)).thenReturn(testProduct);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.addProductReview(request);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Great product!");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}
