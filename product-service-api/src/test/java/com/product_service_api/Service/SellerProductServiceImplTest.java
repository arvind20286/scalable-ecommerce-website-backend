package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.BrandRequestDTO;
import com.product_service_api.Entity.Brand;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.BrandRepository;
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
@DisplayName("SellerProductServiceImpl Unit Tests")
class SellerProductServiceImplTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private SellerProductServiceImpl sellerProductService;

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = new Brand();
        testBrand.setId(1L);
        testBrand.setBrandName("TestBrand");
    }

    @Test
    @DisplayName("Should retrieve all brands successfully")
    void testGetAllBrands_Success() {
        List<Brand> brands = new ArrayList<>();
        brands.add(testBrand);

        when(brandRepository.findAll()).thenReturn(brands);

        List<Brand> result = sellerProductService.getAllBrands();

        assertThat(result).isNotNull().hasSize(1).containsExactly(testBrand);
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should register brand successfully")
    void testRegisterBrand_Success() {
        BrandRequestDTO request = new BrandRequestDTO();
        request.setBrandName("NewBrand");
        request.setBrandDescription("Brand Description");

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(brandRepository.existsByBrandName("NewBrand")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(testBrand);

        Brand result = sellerProductService.registerBrand(request);

        assertThat(result).isNotNull().isEqualTo(testBrand);
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when brand name already exists")
    void testRegisterBrand_DuplicateBrand() {
        BrandRequestDTO request = new BrandRequestDTO();
        request.setBrandName("ExistingBrand");

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(brandRepository.existsByBrandName("ExistingBrand")).thenReturn(true);

        assertThatThrownBy(() -> sellerProductService.registerBrand(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Brand Name Already Exists");
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to register brand")
    void testRegisterBrand_NotAdmin() {
        BrandRequestDTO request = new BrandRequestDTO();

        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> sellerProductService.registerBrand(request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should return null when database error occurs in getAllBrands")
    void testGetAllBrands_Exception() {
        when(brandRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        List<Brand> result = sellerProductService.getAllBrands();

        assertThat(result).isNull();
    }
}
