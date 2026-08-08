package com.product_service_api.repository;

import com.product_service_api.Entity.AttributeType;
import com.product_service_api.Repository.AttributeTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AttributeTypeRepositoryTest {

    @Autowired
    private AttributeTypeRepository attributeTypeRepository;

    @Test
    public void testExistsByAttributeName() {
        // given
        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeName("color");
        attributeTypeRepository.save(attributeType);

        // when
        boolean actual = attributeTypeRepository.existsByAttributeName("color");
        boolean notExists = attributeTypeRepository.existsByAttributeName("size");

        // then
        assertThat(actual).isTrue();
        assertThat(notExists).isFalse();
    }
}
