package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.*;
import com.product_service_api.Entity.*;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttributeServiceImpl Unit Tests")
class AttributeServiceImplTest {

    @Mock
    private ColourRepository colourRepository;

    @Mock
    private SizeCategoryRepository sizeCategoryRepository;

    @Mock
    private SizeOptionRepository sizeOptionRepository;

    @Mock
    private AttributeTypeRepository attributeTypeRepository;

    @Mock
    private AttributeOptionRepository attributeOptionRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private AttributeServiceImpl attributeService;

    private Colour testColour;
    private SizeCategory testSizeCategory;
    private SizeOption testSizeOption;

    @BeforeEach
    void setUp() {
        testColour = new Colour();
        testColour.setId(1L);
        testColour.setColourName("red");

        testSizeCategory = new SizeCategory();
        testSizeCategory.setId(1L);
        testSizeCategory.setCategoryName("Apparel");

        testSizeOption = new SizeOption();
        testSizeOption.setId(1L);
        testSizeOption.setSizeName("M");
        testSizeOption.setSizeCategory(testSizeCategory);
    }

    @Test
    @DisplayName("Should retrieve all colours successfully")
    void testGetAllColours_Success() {
        List<Colour> colours = new ArrayList<>();
        colours.add(testColour);

        when(colourRepository.findAll()).thenReturn(colours);

        List<Colour> result = attributeService.getAllColours();

        assertThat(result).isNotNull().hasSize(1).containsExactly(testColour);
    }

    @Test
    @DisplayName("Should retrieve all size categories successfully")
    void testGetAllSizeCategories_Success() {
        List<SizeCategory> categories = new ArrayList<>();
        categories.add(testSizeCategory);

        when(sizeCategoryRepository.findAll()).thenReturn(categories);

        List<SizeCategory> result = attributeService.getAllSizeCategories();

        assertThat(result).isNotNull().hasSize(1).containsExactly(testSizeCategory);
    }

    @Test
    @DisplayName("Should retrieve all size options successfully")
    void testGetAllSizeOptions_Success() {
        List<SizeOption> options = new ArrayList<>();
        options.add(testSizeOption);

        when(sizeOptionRepository.findAll()).thenReturn(options);

        List<SizeOption> result = attributeService.getAllSizeOptions();

        assertThat(result).isNotNull().hasSize(1).containsExactly(testSizeOption);
    }

    @Test
    @DisplayName("Should add colour successfully")
    void testAddColour_Success() {
        ColourRequest request = new ColourRequest();
        request.setColourName("Blue");

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(colourRepository.existsByColourNameIgnoreCase("Blue")).thenReturn(false);
        when(colourRepository.save(any(Colour.class))).thenReturn(testColour);

        Colour result = attributeService.addColour(request);

        assertThat(result).isNotNull();
        verify(colourRepository, times(1)).save(any(Colour.class));
    }

    @Test
    @DisplayName("Should throw ForbiddenException when adding colour as non-admin")
    void testAddColour_NotAdmin() {
        ColourRequest request = new ColourRequest();

        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> attributeService.addColour(request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw ConflictException when adding duplicate colour")
    void testAddColour_DuplicateColour() {
        ColourRequest request = new ColourRequest();
        request.setColourName("Red");

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(colourRepository.existsByColourNameIgnoreCase("Red")).thenReturn(true);

        assertThatThrownBy(() -> attributeService.addColour(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Colour Already Exists");
    }

    @Test
    @DisplayName("Should add size category successfully")
    void testAddSizeCategory_Success() {
        SizeCategoryRequest request = new SizeCategoryRequest();
        request.setCategoryName("Shoes");

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(sizeCategoryRepository.existsByCategoryName("Shoes")).thenReturn(false);
        when(sizeCategoryRepository.save(any(SizeCategory.class))).thenReturn(testSizeCategory);

        SizeCategory result = attributeService.addSizeCategory(request);

        assertThat(result).isNotNull();
        verify(sizeCategoryRepository, times(1)).save(any(SizeCategory.class));
    }

    @Test
    @DisplayName("Should add size option successfully")
    void testAddSizeOption_Success() {
        SizeOptionRequest request = new SizeOptionRequest();
        request.setSizeName("M");
        request.setSizeCategoryId(1L);
        request.setSizeOrder(2);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(sizeOptionRepository.existsBySizeNameAndSizeCategoryId("M", 1L)).thenReturn(false);
        when(sizeCategoryRepository.getReferenceById(1L)).thenReturn(testSizeCategory);
        when(sizeOptionRepository.save(any(SizeOption.class))).thenReturn(testSizeOption);

        SizeOption result = attributeService.addSizeOption(request);

        assertThat(result).isNotNull();
        verify(sizeOptionRepository, times(1)).save(any(SizeOption.class));
    }

    @Test
    @DisplayName("Should add attribute type successfully")
    void testAddAttributeType_Success() {
        AttributeTypeRequest request = new AttributeTypeRequest();
        request.setAttributeName("Material");

        AttributeType attrType = new AttributeType();
        attrType.setId(1L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(attributeTypeRepository.existsByAttributeName("Material")).thenReturn(false);
        when(attributeTypeRepository.save(any(AttributeType.class))).thenReturn(attrType);

        AttributeType result = attributeService.addAttributeType(request);

        assertThat(result).isNotNull();
        verify(attributeTypeRepository, times(1)).save(any(AttributeType.class));
    }

    @Test
    @DisplayName("Should add attribute option successfully")
    void testAddAttributeOption_Success() {
        AttributeOptionRequest request = new AttributeOptionRequest();
        request.setAttributeOptionName("Cotton");
        request.setAttributeTypeId(1L);

        AttributeType attrType = new AttributeType();
        attrType.setId(1L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(attributeTypeRepository.existsById(1L)).thenReturn(true);
        when(attributeOptionRepository.existsByNameAndAttributeTypeId("Cotton", 1L)).thenReturn(false);
        when(attributeTypeRepository.getReferenceById(1L)).thenReturn(attrType);
        when(attributeOptionRepository.save(any(AttributeOption.class))).thenReturn(new AttributeOption());

        AttributeOption result = attributeService.addAttributeOption(request);

        assertThat(result).isNotNull();
        verify(attributeOptionRepository, times(1)).save(any(AttributeOption.class));
    }
    
    @Test
    @DisplayName("Should return null when database error occurs in getAllColours")
    void testGetAllColours_Exception() {
        when(colourRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        List<Colour> result = attributeService.getAllColours();

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should throw BadRequestException when size category does not exist")
    void testAddSizeOption_CategoryNotFound() {
        SizeOptionRequest request = new SizeOptionRequest();
        request.setSizeName("L");
        request.setSizeCategoryId(999L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(sizeOptionRepository.existsBySizeNameAndSizeCategoryId("L", 999L)).thenReturn(false);
        when(sizeCategoryRepository.getReferenceById(999L))
                .thenThrow(new BadRequestException("Size Category not found"));
        
        assertThatThrownBy(() -> attributeService.addSizeOption(request))
                .isInstanceOf(BadRequestException.class);
    }
}
