package com.product_service_api.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.Entity.ProductImages;
import com.product_service_api.Entity.Review;
import com.product_service_api.Repository.ProductImagesRepository;
import com.product_service_api.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.product_service_api.Entity.Product;
import com.product_service_api.Repository.ProductRepository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImagesRepository productImagesRepository;
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

    @Override
    public Product saveProduct(Product product, List<MultipartFile> images) {
        try {
            productRepository.save(product);
            product.setImages(this.saveWithImage(product.getId(), images));
            return product;
        } catch (Exception e) {
            return null;
        }
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
            Integer stock = product.getStock();
            product.setStock(stock-sale);
            return productRepository.save(product);
        } catch (Exception e) {
            return null;
        }
    }

}
