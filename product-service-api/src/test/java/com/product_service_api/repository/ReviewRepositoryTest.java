package com.product_service_api.repository;

import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.Review;
import com.product_service_api.Repository.ProductRepository;
import com.product_service_api.Repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testExistsByUserIdAndProductId() {
        // given
        Product product = new Product();
        product.setName("Laptop");
        productRepository.save(product);

        Review review = new Review();
        review.setUserId(1L);
        review.setProduct(product);
        reviewRepository.save(review);

        // when
        boolean actual = reviewRepository.existsByUserIdAndProductId(1L, product.getId());
        boolean notExists = reviewRepository.existsByUserIdAndProductId(2L, product.getId());

        // then
        assertThat(actual).isTrue();
        assertThat(notExists).isFalse();
    }
}
