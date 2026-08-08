package com.product_service_api.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.*;
import com.product_service_api.Entity.*;
import com.product_service_api.Exceptions.BadRequestException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final SizeOptionRepository sizeOptionRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final BrandRepository brandRepository;
    private final AuthServiceClient authServiceClient;
    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String bucketRegion;


    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(Long idProduct) {
        if (!productRepository.existsById(idProduct)) {
            throw new BadRequestException("Product with id " + idProduct + " does not exist");
        }
        return productRepository.findById(idProduct).get();

    }

    @Override
    public VariationResponseDTO findProductVariationById(Long variationId) {
        if (!productVariationRepository.existsById(variationId)) {
            throw new BadRequestException("Product Variation with id " + variationId + " does not exist");
        }

        ProductVariation variation = productVariationRepository.findById(variationId).get();
        ProductItem productItem = variation.getProductItem();
        return new VariationResponseDTO(variation.getId(), variation.getStock(), productItem.getSalePrice(), variation.getSizeOption().getId());
    }

    @Override
    @Transactional
    public Product saveProduct(ProductRequest productRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }
        if (!brandRepository.existsById(productRequest.getBrandId()) || !productCategoryRepository.existsById(productRequest.getProductCategoryId())) {
            throw new BadRequestException("Either Brand or Product Category does not exist");
        }
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(brandRepository.getReferenceById(productRequest.getBrandId()));
        product.setProductCategory(productCategoryRepository.getReferenceById(productRequest.getProductCategoryId()));

        List<ProductItem> productItems = new ArrayList<>();
        for (ProductItemData itemData : productRequest.getProductItemList()) {
            ProductItem productItem = new ProductItem();
            productItem.setOriginalPrice(itemData.getOriginalPrice());
            productItem.setSalePrice(itemData.getSalePrice());
            productItem.setProductCode(itemData.getProductCode());
            productItem.setProduct(product);
            if (!colourRepository.existsById(itemData.getColourId())) {
                throw new BadRequestException("Colour with id " + itemData.getColourId() + " does not exist");
            }
            productItem.setColour(colourRepository.getReferenceById(itemData.getColourId()));

            List<ProductVariation> productVariations = new ArrayList<>();
            for (VariationData variationData : itemData.getVariationDataList()) {
                ProductVariation productVariation = new ProductVariation();
                productVariation.setStock(variationData.getStock());
                productVariation.setProductItem(productItem);
                if (!sizeOptionRepository.existsById(variationData.getSizeOptionId())) {
                    throw new BadRequestException("Size Option with id " + variationData.getSizeOptionId() + " does not exist");
                }
                productVariation.setSizeOption(sizeOptionRepository.getReferenceById(variationData.getSizeOptionId()));
                productVariations.add(productVariation);
            }
            productItem.setProductVariationList(productVariations);
            productItems.add(productItem);
        }
        product.setProductItems(productItems);

        List<ProductAttribute> productAttributes = new ArrayList<>();
        for (ProductAttributeData attributeData : productRequest.getProductAttributeDataList()) {
            ProductAttribute productAttribute = new ProductAttribute();
            if (!attributeOptionRepository.existsById(attributeData.getAttributeOptionId())) {
                throw new BadRequestException("Attribute Option with id " + attributeData.getAttributeOptionId() + " does not exist");
            }
            productAttribute.setAttributeOption(attributeOptionRepository.getReferenceById(attributeData.getAttributeOptionId()));
            productAttribute.setProduct(product);
            productAttributes.add(productAttribute);
        }
        product.setProductAttributeList(productAttributes);
        product = productRepository.save(product);
        return productRepository.findById(product.getId()).get();
    }

    @Override
    public Product saveProductWithImages(String productRequestString, Map<String, MultipartFile> imageFiles) throws IOException {
        ProductRequest productRequest = objectMapper.readValue(productRequestString, ProductRequest.class);

        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }
        if (!brandRepository.existsById(productRequest.getBrandId()) || !productCategoryRepository.existsById(productRequest.getProductCategoryId())) {
            throw new BadRequestException("Either Brand or Product Category does not exist");
        }
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(brandRepository.findById(productRequest.getBrandId()).get());
        product.setProductCategory(productCategoryRepository.findById(productRequest.getProductCategoryId()).get());

        List<ProductItem> productItems = new ArrayList<>();
        for (ProductItemData itemData : productRequest.getProductItemList()) {
            ProductItem productItem = new ProductItem();
            productItem.setOriginalPrice(itemData.getOriginalPrice());
            productItem.setSalePrice(itemData.getSalePrice());
            productItem.setProductCode(itemData.getProductCode());
            productItem.setProduct(product);
            if (!colourRepository.existsById(itemData.getColourId())) {
                throw new BadRequestException("Colour with id " + itemData.getColourId() + " does not exist");
            }
            productItem.setColour(colourRepository.findById(itemData.getColourId()).get());

            List<ProductVariation> productVariations = new ArrayList<>();
            for (VariationData variationData : itemData.getVariationDataList()) {
                ProductVariation productVariation = new ProductVariation();
                productVariation.setStock(variationData.getStock());
                productVariation.setProductItem(productItem);
                if (!sizeOptionRepository.existsById(variationData.getSizeOptionId())) {
                    throw new BadRequestException("Size Option with id " + variationData.getSizeOptionId() + " does not exist");
                }
                productVariation.setSizeOption(sizeOptionRepository.findById(variationData.getSizeOptionId()).get());
                productVariations.add(productVariation);
            }
            productItem.setProductVariationList(productVariations);

            if (itemData.getImageFilenames() != null) {
                List<ProductImages> productImages = new ArrayList<>();
                for (String filename : itemData.getImageFilenames()) {
                    MultipartFile imageFile = imageFiles.get(filename);
                    if (imageFile != null) {
                        String imageUrl = "https//:image"; //uploadFileToS3(imageFile);
                        ProductImages image = new ProductImages();
                        image.setUrl(imageUrl);
                        image.setProductItem(productItem);
                        productImages.add(image);
                    }
                }
                productItem.setImages(productImages);
            }
            productItems.add(productItem);
        }
        product.setProductItems(productItems);

        List<ProductAttribute> productAttributes = new ArrayList<>();
        for (ProductAttributeData attributeData : productRequest.getProductAttributeDataList()) {
            ProductAttribute productAttribute = new ProductAttribute();
            if (!attributeOptionRepository.existsById(attributeData.getAttributeOptionId())) {
                throw new BadRequestException("Attribute Option with id " + attributeData.getAttributeOptionId() + " does not exist");
            }
            productAttribute.setAttributeOption(attributeOptionRepository.findById(attributeData.getAttributeOptionId()).get());
            productAttribute.setProduct(product);

            productAttributes.add(productAttribute);
        }
        product.setProductAttributeList(productAttributes);
        return productRepository.save(product);
    }

    private String uploadFileToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return "https://" + bucketName + ".s3." + bucketRegion + ".amazonaws.com/" + fileName;
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductRequest productRequest) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product with id " + productId + " does not exist"));

        if (!brandRepository.existsById(productRequest.getBrandId()) || !productCategoryRepository.existsById(productRequest.getProductCategoryId())) {
            throw new BadRequestException("Either Brand or Product Category does not exist");
        }

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setBrand(brandRepository.getReferenceById(productRequest.getBrandId()));
        existingProduct.setProductCategory(productCategoryRepository.getReferenceById(productRequest.getProductCategoryId()));

        if (existingProduct.getProductItems() != null) {
            existingProduct.getProductItems().clear();
        }

        List<ProductItem> productItems = new ArrayList<>();
        for (ProductItemData itemData : productRequest.getProductItemList()) {
            ProductItem productItem = new ProductItem();
            productItem.setOriginalPrice(itemData.getOriginalPrice());
            productItem.setSalePrice(itemData.getSalePrice());
            productItem.setProductCode(itemData.getProductCode());
            productItem.setProduct(existingProduct);
            if (!colourRepository.existsById(itemData.getColourId())) {
                throw new BadRequestException("Colour with id " + itemData.getColourId() + " does not exist");
            }
            productItem.setColour(colourRepository.getReferenceById(itemData.getColourId()));
            productItem = productItemRepository.save(productItem);

            List<ProductVariation> productVariations = new ArrayList<>();
            for (VariationData variationData : itemData.getVariationDataList()) {
                ProductVariation productVariation = new ProductVariation();
                productVariation.setStock(variationData.getStock());
                productVariation.setProductItem(productItem);
                if (!sizeOptionRepository.existsById(variationData.getSizeOptionId())) {
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

        if (existingProduct.getProductAttributeList() != null) {
            existingProduct.getProductAttributeList().clear();
        }

        List<ProductAttribute> productAttributes = new ArrayList<>();
        for (ProductAttributeData attributeData : productRequest.getProductAttributeDataList()) {
            ProductAttribute productAttribute = new ProductAttribute();
            if (!attributeOptionRepository.existsById(attributeData.getAttributeOptionId())) {
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
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product with id " + productId + " does not exist"));
        productRepository.delete(product);
    }

    @Override
    public List<Product> saveListProducts(List<Product> products) {
        try {
            return productRepository.saveAll(products);
        } catch (Exception e) {
            return null;
        }
    }

//    @Override
//    public List<ProductImages> saveWithImage(Long productId, List<MultipartFile> images) throws IOException {
//        Product product = productRepository.getReferenceById(productId);
//        List<ProductImages> images_entity = new ArrayList<>();
//        for (MultipartFile file : images) {
//            String imageUrl = uploadFileToS3(file);
//            ProductImages image = new ProductImages();
//            image.setUrl(imageUrl);
//            image.setProduct(product);
//            try {
//                images_entity.add(productImagesRepository.save(image));
//            } catch (Exception e) {
//                return null;
//            }
//        }
//        return images_entity;
//    }

    @Override
    public Product updateStockProduct(Long idProduct, Integer sale) {
        try {
            Product product = productRepository.findById(idProduct).get();
            // Integer stock = product.getStock();
            // product.setStock(stock-sale);
            return productRepository.save(product);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductImages> getAllProductImages() {
        try {
            return productImagesRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductVariation> getAllProductVariations() {
        try {
            return productVariationRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }
}
