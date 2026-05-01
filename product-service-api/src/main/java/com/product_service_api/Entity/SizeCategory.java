package com.product_service_api.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="size_category")
@Entity
public class SizeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "sizeCategory")
    private List<ProductCategory> productCategories;

    @JsonManagedReference
    @OneToMany(mappedBy = "sizeCategory", cascade = CascadeType.ALL)
    private List<SizeOption> sizeOptionList;
}
