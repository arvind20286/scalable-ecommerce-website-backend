package com.product_service_api.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="product_category")
@Entity
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String category;

    @ManyToOne
    private SizeCategory sizeCategory;

    @Column
    private String categoryDescription;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private ProductCategory parentCategoryId;

    @OneToMany(mappedBy = "parentCategoryId", cascade = CascadeType.ALL)
    private List<ProductCategory> subCategories;

    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL)
    private List<Product> productsList;

}
