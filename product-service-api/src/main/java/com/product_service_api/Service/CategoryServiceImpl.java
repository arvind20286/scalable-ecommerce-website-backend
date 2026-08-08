package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ProductCategoryRequest;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.ProductCategoryRepository;
import com.product_service_api.Repository.SizeCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final SizeCategoryRepository sizeCategoryRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public ProductCategory addProductCategory(ProductCategoryRequest productCategoryRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        if (productCategoryRepository.existsByCategory(productCategoryRequest.getCategory())) {
            throw new ConflictException("Product Category with name '" + productCategoryRequest.getCategory() + "' already exists");
        }

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(productCategoryRequest.getCategory());
        productCategory.setCategoryDescription(productCategoryRequest.getCategoryDescription());

        if (productCategoryRequest.getSizeCategoryId() != null && productCategoryRequest.getSizeCategoryId() > 0) {
            if (!sizeCategoryRepository.existsById(productCategoryRequest.getSizeCategoryId())) {
                throw new BadRequestException("Size Category with id " + productCategoryRequest.getSizeCategoryId() + " does not exist");
            }
            productCategory.setSizeCategory(sizeCategoryRepository.getReferenceById(productCategoryRequest.getSizeCategoryId()));
        }

        if (productCategoryRequest.getParentCategoryId() != null && productCategoryRequest.getParentCategoryId() > 0) {
            if (!productCategoryRepository.existsById(productCategoryRequest.getParentCategoryId())) {
                throw new BadRequestException("Parent Category with id " + productCategoryRequest.getParentCategoryId() + " does not exist");
            }
            productCategory.setParentCategory(productCategoryRepository.getReferenceById(productCategoryRequest.getParentCategoryId()));
        }

        return productCategoryRepository.save(productCategory);
    }

    @Override
    @Cacheable(value = "productCategories", unless = "#result == null")
    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepository.findAll();
    }

    @Override
    public ProductCategory getProductCategoryById(Long categoryId) {
        return productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Product Category with id " + categoryId + " does not exist"));
    }

    @Override
    @Transactional
    public ProductCategory updateProductCategory(Long categoryId, ProductCategoryRequest productCategoryRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        ProductCategory existingCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Product Category with id " + categoryId + " does not exist"));

        if (!existingCategory.getCategory().equals(productCategoryRequest.getCategory()) &&
                productCategoryRepository.existsByCategory(productCategoryRequest.getCategory())) {
            throw new ConflictException("Product Category with name '" + productCategoryRequest.getCategory() + "' already exists");
        }

        existingCategory.setCategory(productCategoryRequest.getCategory());
        existingCategory.setCategoryDescription(productCategoryRequest.getCategoryDescription());

        if (productCategoryRequest.getSizeCategoryId() != null && productCategoryRequest.getSizeCategoryId() > 0) {
            if (!sizeCategoryRepository.existsById(productCategoryRequest.getSizeCategoryId())) {
                throw new BadRequestException("Size Category with id " + productCategoryRequest.getSizeCategoryId() + " does not exist");
            }
            existingCategory.setSizeCategory(sizeCategoryRepository.getReferenceById(productCategoryRequest.getSizeCategoryId()));
        } else {
            existingCategory.setSizeCategory(null);
        }

        if (productCategoryRequest.getParentCategoryId() != null && productCategoryRequest.getParentCategoryId() > 0) {
            if (!productCategoryRepository.existsById(productCategoryRequest.getParentCategoryId())) {
                throw new BadRequestException("Parent Category with id " + productCategoryRequest.getParentCategoryId() + " does not exist");
            }
            existingCategory.setParentCategory(productCategoryRepository.getReferenceById(productCategoryRequest.getParentCategoryId()));
        } else {
            existingCategory.setParentCategory(null);
        }

        return productCategoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "productCategories", allEntries = true)
    public boolean deleteProductCategory(Long categoryId) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Product Category with id " + categoryId + " does not exist"));

        productCategoryRepository.delete(category);
        return false;
    }

    @Override
    public List<ProductCategory> getParentProductCategories() {
        try {
            return productCategoryRepository.findAllByParentCategoryIsNull();
        } catch (Exception e) {
            return null;
        }
    }


}
