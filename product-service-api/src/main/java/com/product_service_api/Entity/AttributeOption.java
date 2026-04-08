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
@Table(name = "attribute_option")
public class AttributeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "attributeOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAttribute> productAttributeList;

    @ManyToOne
    private AttributeType attributeType;
}
