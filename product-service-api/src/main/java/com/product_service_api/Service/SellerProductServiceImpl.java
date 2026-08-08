package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.BrandRequestDTO;
import com.product_service_api.Entity.Brand;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerProductServiceImpl implements SellerProductService {

    private final BrandRepository brandRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    public Brand registerBrand(BrandRequestDTO brandRequestDTO) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised");
        }

        if (brandRepository.existsByBrandName(brandRequestDTO.getBrandName())) {
            throw new BadRequestException("Brand Name Already Exists");
        }
        Brand brand = new Brand();
        brand.setBrandName(brandRequestDTO.getBrandName());
        brand.setBrandDescription(brandRequestDTO.getBrandDescription());
        brand = brandRepository.save(brand);
        return brand;
    }

    @Override
    @Cacheable(value = "brands", unless = "#result == null")
    public List<Brand> getAllBrands() {
        try {
            return brandRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
}
