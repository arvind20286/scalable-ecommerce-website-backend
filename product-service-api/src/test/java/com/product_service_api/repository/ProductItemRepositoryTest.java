package com.product_service_api.repository;

import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductItem;
import com.product_service_api.Repository.ProductItemRepository;
import com.product_service_api.Repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductItemRepositoryTest {

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testFindAllByProductIdIn() {
        // given
        Product product1 = new Product();
        product1.setName("Product 1");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        productRepository.save(product2);

        ProductItem item1 = new ProductItem();
        item1.setProduct(product1);
        productItemRepository.save(item1);

        ProductItem item2 = new ProductItem();
        item2.setProduct(product2);
        productItemRepository.save(item2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<ProductItem> items = productItemRepository.findAllByProductIdIn(List.of(product1.getId()), pageable);

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getProduct().getId()).isEqualTo(product1.getId());
    }
}
