package com.product_service_api.repository;

import com.product_service_api.Entity.AttributeOption;
import com.product_service_api.Entity.AttributeType;
import com.product_service_api.Repository.AttributeOptionRepository;
import com.product_service_api.Repository.AttributeTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AttributeOptionRepositoryTest {

    @Autowired
    private AttributeOptionRepository attributeOptionRepository;

    @Autowired
    private AttributeTypeRepository attributeTypeRepository;

    @Test
    public void testExistsByNameAndAttributeTypeId() {
        // given
        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeName("color");
        attributeTypeRepository.save(attributeType);

        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setName("red");
        attributeOption.setAttributeType(attributeType);
        attributeOptionRepository.save(attributeOption);

        // when
        boolean actual = attributeOptionRepository.existsByNameAndAttributeTypeId("red", attributeType.getId());

        // then
        assertThat(actual).isTrue();
    }
}
