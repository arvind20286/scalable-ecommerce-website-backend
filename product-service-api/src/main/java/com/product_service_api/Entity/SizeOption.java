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
@Table(name = "size_option")
public class SizeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sizeName;

    @Column
    private Integer sizeOrder;

    @ManyToOne
    private SizeCategory sizeCategory;

    @OneToMany(mappedBy = "sizeOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariation> productVariationList;

}
