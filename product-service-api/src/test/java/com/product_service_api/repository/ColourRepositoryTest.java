package com.product_service_api.repository;

import com.product_service_api.Entity.Colour;
import com.product_service_api.Repository.ColourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ColourRepositoryTest {

    @Autowired
    private ColourRepository colourRepository;

    @Test
    public void testExistsByColourNameIgnoreCase() {
        // given
        Colour colour = new Colour();
        colour.setColourName("Red");
        colourRepository.save(colour);

        // when
        boolean actual = colourRepository.existsByColourNameIgnoreCase("red");
        boolean actualUpper = colourRepository.existsByColourNameIgnoreCase("RED");
        boolean notExists = colourRepository.existsByColourNameIgnoreCase("blue");

        // then
        assertThat(actual).isTrue();
        assertThat(actualUpper).isTrue();
        assertThat(notExists).isFalse();
    }
}
