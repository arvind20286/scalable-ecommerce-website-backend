package com.product_service_api.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_item")
public class ProductItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double originalPrice;

    @Column
    private Double salePrice;

    @Column(unique = true)
    private String productCode;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Colour colour;

    @OneToMany(mappedBy = "productItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariation> productVariationList;

}
