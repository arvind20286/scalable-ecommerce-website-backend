package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.*;
import com.product_service_api.Entity.AttributeOption;
import com.product_service_api.Entity.AttributeType;
import com.product_service_api.Entity.Colour;
import com.product_service_api.Entity.SizeCategory;
import com.product_service_api.Entity.SizeOption;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final ColourRepository colourRepository;
    private final SizeCategoryRepository sizeCategoryRepository;
    private final SizeOptionRepository sizeOptionRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    @CacheEvict(value = "colours", allEntries = true)
    public Colour addColour(ColourRequest colourRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }
        if (colourRepository.existsByColourNameIgnoreCase(colourRequest.getColourName())) {
            throw new ConflictException("Colour Already Exists");
        }
        Colour colourObj = new Colour();
        colourObj.setColourName(colourRequest.getColourName().toLowerCase(Locale.ROOT));
        colourRepository.save(colourObj);
        return colourObj;
    }

    @Override
    @Cacheable(value = "colours", unless = "#result == null")
    public List<Colour> getAllColours() {
        return colourRepository.findAll();
    }

    @Override
    public SizeCategory addSizeCategory(SizeCategoryRequest sizeCategoryRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised");
        }

        if (sizeCategoryRepository.existsByCategoryName(sizeCategoryRequest.getCategoryName())) {
            throw new BadRequestException("Size Category Already Exists");
        }
        SizeCategory sizeCategory = new SizeCategory();
        sizeCategory.setCategoryName(sizeCategoryRequest.getCategoryName());
        sizeCategory = sizeCategoryRepository.save(sizeCategory);
        return sizeCategory;
    }

    @Override
    @Cacheable(value = "sizeCategories", unless = "#result == null")
    public List<SizeCategory> getAllSizeCategories() {
        return sizeCategoryRepository.findAll();
    }

    @Override
    public SizeOption addSizeOption(SizeOptionRequest sizeOptionRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised");
        }
        if (sizeOptionRepository.existsBySizeNameAndSizeCategoryId(sizeOptionRequest.getSizeName(), sizeOptionRequest.getSizeCategoryId())) {
            throw new BadRequestException("Size Category Already Exists");
        }
        SizeOption sizeOption = new SizeOption();
        sizeOption.setSizeName(sizeOptionRequest.getSizeName());
        sizeOption.setSizeOrder(sizeOptionRequest.getSizeOrder());
        sizeOption.setSizeCategory(sizeCategoryRepository.getReferenceById(sizeOptionRequest.getSizeCategoryId()));
        sizeOption = sizeOptionRepository.save(sizeOption);
        return sizeOption;
    }

    @Override
    @Cacheable(value = "sizeOptions", unless = "#result == null")
    public List<SizeOption> getAllSizeOptions() {
        return sizeOptionRepository.findAll();
    }

    @Override
    public AttributeType addAttributeType(AttributeTypeRequest attributeTypeRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        if (attributeTypeRepository.existsByAttributeName(attributeTypeRequest.getAttributeName())) {
            throw new BadRequestException("Attribute Type Already Exists");
        }

        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeName(attributeTypeRequest.getAttributeName());
        attributeType = attributeTypeRepository.save(attributeType);
        return attributeType;
    }

    @Override
    @Cacheable(value = "attributeTypes", unless = "#result == null")
    public List<AttributeType> getAllAttributeTypes() {
        return attributeTypeRepository.findAll();
    }

    @Override
    public AttributeOption addAttributeOption(AttributeOptionRequest attributeOptionRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        if (!attributeTypeRepository.existsById(attributeOptionRequest.getAttributeTypeId())) {
            throw new BadRequestException("Attribute Type Does Not Exists");
        }

        if (attributeOptionRepository.existsByNameAndAttributeTypeId(attributeOptionRequest.getAttributeOptionName(), attributeOptionRequest.getAttributeTypeId())) {
            throw new BadRequestException("Attribute Option Already Exists");
        }

        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setName(attributeOptionRequest.getAttributeOptionName());
        attributeOption.setAttributeType(attributeTypeRepository.getReferenceById(attributeOptionRequest.getAttributeTypeId()));
        attributeOption = attributeOptionRepository.save(attributeOption);
        return attributeOption;
    }

    @Override
    @Cacheable(value = "attributeOptions", unless = "#result == null")
    public List<AttributeOption> getAllAttributeOptions() {
        return attributeOptionRepository.findAll();
    }
}
