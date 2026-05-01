package com.product_service_api.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.*;
import com.product_service_api.Entity.*;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImagesRepository productImagesRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ColourRepository colourRepository;
    private final SizeCategoryRepository sizeCategoryRepository;
    private final SizeOptionRepository sizeOptionRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final BrandRepository brandRepository;
    private final ReviewRepository reviewRepository;
    private final AuthServiceClient authServiceClient;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private  String bucketRegion;

    @Override
    public List<Product> findAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            return null;
        }

    }
    @Override
    public Product findProductById(Long idProduct) {
        try {
            return productRepository.findById(idProduct).get();
        } catch (Exception e) {
            return null;
        }

    }

//    @Override
//    public Product saveProduct(Product product, List<MultipartFile> images) {
//        try {
//            productRepository.save(product);
//            product.setImages(this.saveWithImage(product.getId(), images));
//            return product;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Override
    @Transactional
    public Product saveProduct(ProductRequest productRequest) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        if(!brandRepository.existsById(productRequest.getBrandId()) || !productCategoryRepository.existsById(productRequest.getProductCategoryId())){
            throw new BadRequestException("Either Brand or Product Category does not exist");
        }
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(brandRepository.getReferenceById(productRequest.getBrandId()));
        product.setProductCategory(productCategoryRepository.getReferenceById(productRequest.getProductCategoryId()));
        product = productRepository.save(product);
        
        List<ProductItem> productItems = new ArrayList<>();
        for(ProductItemData itemData : productRequest.getProductItemList()){
            ProductItem productItem = new ProductItem();
            productItem.setOriginalPrice(itemData.getOriginalPrice());
            productItem.setSalePrice(itemData.getSalePrice());
            productItem.setProductCode(itemData.getProductCode());
            productItem.setProduct(product);
            if(!colourRepository.existsById(itemData.getColourId())){
                throw new BadRequestException("Colour with id " + itemData.getColourId() + " does not exist");
            }
            productItem.setColour(colourRepository.getReferenceById(itemData.getColourId()));
            productItem = productItemRepository.save(productItem);
            
            List<ProductVariation> productVariations = new ArrayList<>();
            for(VariationData variationData : itemData.getVariationDataList()){
                ProductVariation productVariation = new ProductVariation();
                productVariation.setStock(variationData.getStock());
                productVariation.setProductItem(productItem);
                if(!sizeOptionRepository.existsById(variationData.getSizeOptionId())){
                    throw new BadRequestException("Size Option with id " + variationData.getSizeOptionId() + " does not exist");
                }
                productVariation.setSizeOption(sizeOptionRepository.getReferenceById(variationData.getSizeOptionId()));
                productVariation = productVariationRepository.save(productVariation);
                productVariations.add(productVariation);
            }
            productItem.setProductVariationList(productVariations);
            productItems.add(productItem);
        }
        product.setProductItems(productItems);
        
        List<ProductAttribute> productAttributes = new ArrayList<>();
        for(ProductAttributeData attributeData : productRequest.getProductAttributeDataList()){
            ProductAttribute productAttribute = new ProductAttribute();
            if(!attributeOptionRepository.existsById(attributeData.getAttributeOptionId())){
                throw new BadRequestException("Attribute Option with id " + attributeData.getAttributeOptionId() + " does not exist");
            }
            productAttribute.setAttributeOption(attributeOptionRepository.getReferenceById(attributeData.getAttributeOptionId()));
            productAttribute.setProduct(product);
            productAttribute = productAttributeRepository.save(productAttribute);
            productAttributes.add(productAttribute);
        }
        product.setProductAttributeList(productAttributes);
        return productRepository.save(product);
    }

    @Override
    public List<ProductImages> saveWithImage(Long productId, List<MultipartFile> images) throws IOException {
        Product product = productRepository.getReferenceById(productId);
        List<ProductImages> images_entity = new ArrayList<>();
        for(MultipartFile file : images) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            String fileUrl = "https://" + bucketName + ".s3." + bucketRegion + ".amazonaws.com/" + fileName;

            System.out.println(fileUrl);
            ProductImages productImages = new ProductImages();
            productImages.setProduct(product);
            productImages.setUrl(fileUrl);

            try {
                images_entity.add(productImagesRepository.save(productImages));
            }
            catch (Exception e){
                return null;
            }
        }
        return images_entity;
    }

    @Override
    public Colour addColour(ColourRequest colourRequest){
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        if(colourRepository.existsByColourNameIgnoreCase(colourRequest.getColourName())){
            throw new ConflictException("Colour Already Exists");
        }
        Colour colourObj = new Colour();
        colourObj.setColourName(colourRequest.getColourName().toLowerCase(Locale.ROOT));
        colourRepository.save(colourObj);
        return colourObj;
    }

    @Override
    public List<Colour> getAllColours(){
        try {
            return colourRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SizeCategory addSizeCategory(SizeCategoryRequest sizeCategoryRequest){
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised");
        }

        if(sizeCategoryRepository.existsByCategoryName(sizeCategoryRequest.getCategoryName())){
            throw new BadRequestException("Size Category Already Exists");
        }
        SizeCategory sizeCategory = new SizeCategory();
        sizeCategory.setCategoryName(sizeCategoryRequest.getCategoryName());
        sizeCategory = sizeCategoryRepository.save(sizeCategory);
        return sizeCategory;
    }

    @Override
    public List<SizeCategory> getAllSizeCategories(){
        try {
            return sizeCategoryRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public SizeOption addSizeOption(SizeOptionRequest sizeOptionRequest){
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised");
        }
        System.out.println(sizeOptionRequest.getSizeCategoryId());
        if(sizeOptionRepository.existsBySizeNameAndSizeCategoryId(sizeOptionRequest.getSizeName(), sizeOptionRequest.getSizeCategoryId())){
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
    public List<SizeOption> getAllSizeOptions(){
        try {
            return sizeOptionRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AttributeType addAttributeType(AttributeTypeRequest attributeTypeRequest){
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }

        if(attributeTypeRepository.existsByAttributeName(attributeTypeRequest.getAttributeName())){
            throw new BadRequestException("Attribute Type Already Exists");
        }

        AttributeType attributeType = new AttributeType();
        attributeType.setAttributeName(attributeTypeRequest.getAttributeName());
        attributeType = attributeTypeRepository.save(attributeType);
        return attributeType;
    }

    @Override
    public List<AttributeType> getAllAttributeTypes(){
        try {
            return attributeTypeRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AttributeOption addAttributeOption(AttributeOptionRequest attributeOptionRequest){
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }

        if(!attributeTypeRepository.existsById(attributeOptionRequest.getAttributeTypeId())){
            throw new BadRequestException("Attribute Type Does Not Exists");
        }

        if(attributeOptionRepository.existsByNameAndAttributeTypeId(attributeOptionRequest.getAttributeOptionName(), attributeOptionRequest.getAttributeTypeId())){
            throw new BadRequestException("Attribute Option Already Exists");
        }

        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setName(attributeOptionRequest.getAttributeOptionName());
        attributeOption.setAttributeType(attributeTypeRepository.getReferenceById(attributeOptionRequest.getAttributeTypeId()));
        attributeOption = attributeOptionRepository.save(attributeOption);
        return attributeOption;
    }

    @Override
    public List<AttributeOption> getAllAttributeOptions(){
        try {
            return attributeOptionRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Brand registerBrand(BrandRequestDTO brandRequestDTO){
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised");
        }

        if(brandRepository.existsByBrandName(brandRequestDTO.getBrandName())){
            throw new BadRequestException("Brand Name Already Exists");
        }
        Brand brand = new Brand();
        brand.setBrandName(brandRequestDTO.getBrandName());
        brand.setBrandDescription(brandRequestDTO.getBrandDescription());
        brandRepository.save(brand);
        return brand;
    }

    @Override
    public List<Brand> getAllBrands(){
        try {
            return brandRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Review addProductReview(ReviewRequestDTO reviewRequestDTO) {
        Long userId = authServiceClient.getUserIdJWT().getUserId();
        if(!productRepository.existsById(reviewRequestDTO.getProductId()) || reviewRepository.existsByUserIdAndProductId(userId, reviewRequestDTO.getProductId())){
            throw new RuntimeException("Either product does not exist or review already exists");
        }
        Product product = productRepository.getReferenceById(reviewRequestDTO.getProductId());
        Review review = new Review(null, userId, reviewRequestDTO.getRating(), reviewRequestDTO.getComment(), product, null);
        reviewRepository.save(review);
        return review;
    }

    @Override
    public List<Review> getAllReviews(){
        try {
            return reviewRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductImages> getAllProductImages(){
        try {
            return productImagesRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductCategory> getAllProductCategories(){
        try {
            return productCategoryRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductAttribute> getAllProductAttributes(){
        try {
            return productAttributeRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductItem> getAllProductItems(){
        try {
            return productItemRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductVariation> getAllProductVariations(){
        try {
            return productVariationRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Product> saveListProducts(List<Product> products) {
        try {
            return productRepository.saveAll(products);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Product updateStockProduct(Long idProduct, Integer sale) {
        try {
            Product product = productRepository.findById(idProduct).get();
//            Integer stock = product.getStock();
//            product.setStock(stock-sale);
            return productRepository.save(product);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductRequest productRequest) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product with id " + productId + " does not exist"));
        
        if(!brandRepository.existsById(productRequest.getBrandId()) || !productCategoryRepository.existsById(productRequest.getProductCategoryId())){
            throw new BadRequestException("Either Brand or Product Category does not exist");
        }
        
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setBrand(brandRepository.getReferenceById(productRequest.getBrandId()));
        existingProduct.setProductCategory(productCategoryRepository.getReferenceById(productRequest.getProductCategoryId()));
        
        // Clear and update product items
        if(existingProduct.getProductItems() != null) {
            existingProduct.getProductItems().clear();
        }
        
        List<ProductItem> productItems = new ArrayList<>();
        for(ProductItemData itemData : productRequest.getProductItemList()){
            ProductItem productItem = new ProductItem();
            productItem.setOriginalPrice(itemData.getOriginalPrice());
            productItem.setSalePrice(itemData.getSalePrice());
            productItem.setProductCode(itemData.getProductCode());
            productItem.setProduct(existingProduct);
            if(!colourRepository.existsById(itemData.getColourId())){
                throw new BadRequestException("Colour with id " + itemData.getColourId() + " does not exist");
            }
            productItem.setColour(colourRepository.getReferenceById(itemData.getColourId()));
            productItem = productItemRepository.save(productItem);
            
            List<ProductVariation> productVariations = new ArrayList<>();
            for(VariationData variationData : itemData.getVariationDataList()){
                ProductVariation productVariation = new ProductVariation();
                productVariation.setStock(variationData.getStock());
                productVariation.setProductItem(productItem);
                if(!sizeOptionRepository.existsById(variationData.getSizeOptionId())){
                    throw new BadRequestException("Size Option with id " + variationData.getSizeOptionId() + " does not exist");
                }
                productVariation.setSizeOption(sizeOptionRepository.getReferenceById(variationData.getSizeOptionId()));
                productVariation = productVariationRepository.save(productVariation);
                productVariations.add(productVariation);
            }
            productItem.setProductVariationList(productVariations);
            productItems.add(productItem);
        }
        existingProduct.setProductItems(productItems);
        
        // Clear and update product attributes
        if(existingProduct.getProductAttributeList() != null) {
            existingProduct.getProductAttributeList().clear();
        }
        
        List<ProductAttribute> productAttributes = new ArrayList<>();
        for(ProductAttributeData attributeData : productRequest.getProductAttributeDataList()){
            ProductAttribute productAttribute = new ProductAttribute();
            if(!attributeOptionRepository.existsById(attributeData.getAttributeOptionId())){
                throw new BadRequestException("Attribute Option with id " + attributeData.getAttributeOptionId() + " does not exist");
            }
            productAttribute.setAttributeOption(attributeOptionRepository.getReferenceById(attributeData.getAttributeOptionId()));
            productAttribute.setProduct(existingProduct);
            productAttribute = productAttributeRepository.save(productAttribute);
            productAttributes.add(productAttribute);
        }
        existingProduct.setProductAttributeList(productAttributes);
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product with id " + productId + " does not exist"));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductCategory addProductCategory(ProductCategoryRequest productCategoryRequest) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        
        if(productCategoryRepository.existsByCategory(productCategoryRequest.getCategory())){
            throw new ConflictException("Product Category with name '" + productCategoryRequest.getCategory() + "' already exists");
        }
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(productCategoryRequest.getCategory());
        productCategory.setCategoryDescription(productCategoryRequest.getCategoryDescription());
        
        if(productCategoryRequest.getSizeCategoryId() != null && productCategoryRequest.getSizeCategoryId() > 0){
            if(!sizeCategoryRepository.existsById(productCategoryRequest.getSizeCategoryId())){
                throw new BadRequestException("Size Category with id " + productCategoryRequest.getSizeCategoryId() + " does not exist");
            }
            productCategory.setSizeCategory(sizeCategoryRepository.getReferenceById(productCategoryRequest.getSizeCategoryId()));
        }
        
        if(productCategoryRequest.getParentCategoryId() != null && productCategoryRequest.getParentCategoryId() > 0){
            if(!productCategoryRepository.existsById(productCategoryRequest.getParentCategoryId())){
                throw new BadRequestException("Parent Category with id " + productCategoryRequest.getParentCategoryId() + " does not exist");
            }
            productCategory.setParentCategoryId(productCategoryRepository.getReferenceById(productCategoryRequest.getParentCategoryId()));
        }
        
        return productCategoryRepository.save(productCategory);
    }

    @Override
    @Transactional
    public ProductCategory updateProductCategory(Long categoryId, ProductCategoryRequest productCategoryRequest) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        
        ProductCategory existingCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Product Category with id " + categoryId + " does not exist"));
        
        if(!existingCategory.getCategory().equals(productCategoryRequest.getCategory()) && 
           productCategoryRepository.existsByCategory(productCategoryRequest.getCategory())){
            throw new ConflictException("Product Category with name '" + productCategoryRequest.getCategory() + "' already exists");
        }
        
        existingCategory.setCategory(productCategoryRequest.getCategory());
        existingCategory.setCategoryDescription(productCategoryRequest.getCategoryDescription());
        
        if(productCategoryRequest.getSizeCategoryId() != null && productCategoryRequest.getSizeCategoryId() > 0){
            if(!sizeCategoryRepository.existsById(productCategoryRequest.getSizeCategoryId())){
                throw new BadRequestException("Size Category with id " + productCategoryRequest.getSizeCategoryId() + " does not exist");
            }
            existingCategory.setSizeCategory(sizeCategoryRepository.getReferenceById(productCategoryRequest.getSizeCategoryId()));
        } else {
            existingCategory.setSizeCategory(null);
        }
        
        if(productCategoryRequest.getParentCategoryId() != null && productCategoryRequest.getParentCategoryId() > 0){
            if(!productCategoryRepository.existsById(productCategoryRequest.getParentCategoryId())){
                throw new BadRequestException("Parent Category with id " + productCategoryRequest.getParentCategoryId() + " does not exist");
            }
            existingCategory.setParentCategoryId(productCategoryRepository.getReferenceById(productCategoryRequest.getParentCategoryId()));
        } else {
            existingCategory.setParentCategoryId(null);
        }
        
        return productCategoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteProductCategory(Long categoryId) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Product Category with id " + categoryId + " does not exist"));
        
        productCategoryRepository.delete(category);
    }

    @Override
    @Transactional
    public ProductItem updateProductItemStock(Long productItemId, Double originalPrice, Double salePrice) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        
        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new BadRequestException("Product Item with id " + productItemId + " does not exist"));
        
        if(originalPrice != null) {
            productItem.setOriginalPrice(originalPrice);
        }
        if(salePrice != null) {
            productItem.setSalePrice(salePrice);
        }
        
        return productItemRepository.save(productItem);
    }

    @Override
    @Transactional
    public void deleteProductItem(Long productItemId) {
        if(!authServiceClient.isAdmin()){
            throw new ForbiddenException("User not authorised to perform the action");
        }
        
        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new BadRequestException("Product Item with id " + productItemId + " does not exist"));
        
        productItemRepository.delete(productItem);
    }

}
