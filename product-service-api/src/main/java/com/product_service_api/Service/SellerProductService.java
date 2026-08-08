package com.product_service_api.Service;

import com.product_service_api.DTO.BrandRequestDTO;
import com.product_service_api.Entity.Brand;

import java.util.List;

public interface SellerProductService {
    Brand registerBrand(BrandRequestDTO brandRequestDTO);
    List<Brand> getAllBrands();
}
