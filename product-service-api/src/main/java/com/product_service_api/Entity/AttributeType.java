package com.product_service_api.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attribute_type")
public class AttributeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String attributeName;

    @OneToMany(mappedBy = "attributeType", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AttributeOption> attributeOptionList;
}
