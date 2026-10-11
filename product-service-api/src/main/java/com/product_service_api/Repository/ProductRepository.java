package com.product_service_api.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.product_service_api.Entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByProductCategoryId(Long id);

    @Query(
            value = "select p from Product p where p.productCategory.id in :categoryIds " +
                    "order by (select min(item.salePrice) from ProductItem item where item.product = p) asc, p.id asc",
            countQuery = "select count(p) from Product p where p.productCategory.id in :categoryIds"
    )
    Page<Product> findPageByCategoryIdsSortedByLowestSalePriceAsc(
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable
    );

    @Query(
            value = "select p from Product p where p.productCategory.id in :categoryIds " +
                    "order by (select min(item.salePrice) from ProductItem item where item.product = p) desc, p.id asc",
            countQuery = "select count(p) from Product p where p.productCategory.id in :categoryIds"
    )
    Page<Product> findPageByCategoryIdsSortedByLowestSalePriceDesc(
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable
    );

    @Query(
            value = "select p from Product p " +
                    "order by (select min(item.salePrice) from ProductItem item where item.product = p) asc, p.id asc",
            countQuery = "select count(p) from Product p"
    )
    Page<Product> findPageSortedByLowestSalePriceAsc(Pageable pageable);

    @Query(
            value = "select p from Product p " +
                    "order by (select min(item.salePrice) from ProductItem item where item.product = p) desc, p.id asc",
            countQuery = "select count(p) from Product p"
    )
    Page<Product> findPageSortedByLowestSalePriceDesc(
            Pageable pageable
    );
}