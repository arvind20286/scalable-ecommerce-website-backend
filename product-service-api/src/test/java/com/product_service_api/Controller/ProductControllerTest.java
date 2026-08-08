//// DONE BY COPILOT: Integration Tests for ProductController
//package com.product_service_api.Controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.product_service_api.Authorization.Client.AuthServiceClient;
//import com.product_service_api.DTO.*;
//import com.product_service_api.Entity.Product;
//import com.product_service_api.Entity.Brand;
//import com.product_service_api.Entity.Colour;
//import com.product_service_api.Service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//// DONE BY COPILOT: Product Controller Integration Test Suite
//@SpringBootTest
//@AutoConfigureMockMvc
//@DisplayName("ProductController Integration Tests")
//class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private ProductService productService;
//
//    @MockBean
//    private AuthServiceClient authServiceClient;
//
//    private Product testProduct;
//    private Brand testBrand;
//    private Colour testColour;
//
//    @BeforeEach
//    void setUp() {
//        // DONE BY COPILOT: Initialize test data
//        testBrand = new Brand();
//        testBrand.setId(1L);
//        testBrand.setBrandName("TestBrand");
//
//        testColour = new Colour();
//        testColour.setId(1L);
//        testColour.setColourName("Red");
//
//        testProduct = new Product();
//        testProduct.setId(1L);
//        testProduct.setName("Test Product");
//        testProduct.setDescription("Test Description");
//        testProduct.setBrand(testBrand);
//    }
//
//    // ==================== GET ALL PRODUCTS ====================
//
//    @Test
//    @DisplayName("Should return all products with 200 OK status")
//    void testFindAllProducts_Success() throws Exception {
//        // DONE BY COPILOT: Test GET /api/product endpoint
//        List<Product> products = new ArrayList<>();
//        products.add(testProduct);
//
//        when(authServiceClient.isUser()).thenReturn(true);
//        when(productService.findAllProducts()).thenReturn(products);
//
//        mockMvc.perform(get("/api/product")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].name", is("Test Product")));
//
//        verify(productService, times(1)).findAllProducts();
//    }
//
//    @Test
//    @DisplayName("Should return 405 when user is not authorized")
//    void testFindAllProducts_NotUser() throws Exception {
//        // DONE BY COPILOT: Test authorization for product listing
//        when(authServiceClient.isUser()).thenReturn(false);
//
//        mockMvc.perform(get("/api/product")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isMethodNotAllowed());
//    }
//
//    // ==================== GET PRODUCT BY ID ====================
//
//    @Test
//    @DisplayName("Should return product by ID with 200 OK status")
//    void testFindProductById_Success() throws Exception {
//        // DONE BY COPILOT: Test GET /api/product/{id} endpoint
//        when(authServiceClient.isUser()).thenReturn(true);
//        when(productService.findProductById(1L)).thenReturn(testProduct);
//
//        mockMvc.perform(get("/api/product/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.name", is("Test Product")));
//    }
//
//    @Test
//    @DisplayName("Should return 404 when product not found")
//    void testFindProductById_NotFound() throws Exception {
//        // DONE BY COPILOT: Test product not found scenario
//        when(authServiceClient.isUser()).thenReturn(true);
//        when(productService.findProductById(999L)).thenReturn(null);
//
//        mockMvc.perform(get("/api/product/999")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    // ==================== CREATE PRODUCT ====================
//
//    @Test
//    @DisplayName("Should create product successfully with 200 OK status")
//    void testSaveProduct_Success() throws Exception {
//        // DONE BY COPILOT: Test POST /api/product/save endpoint
//        ProductRequest request = new ProductRequest();
//        request.setName("New Product");
//        request.setDescription("New Description");
//        request.setBrandId(1L);
//        request.setProductCategoryId(1L);
//        request.setProductItemList(new ArrayList<>());
//        request.setProductAttributeDataList(new ArrayList<>());
//
//        when(authServiceClient.isAdmin()).thenReturn(true);
//        when(productService.saveProduct(any(ProductRequest.class))).thenReturn(testProduct);
//
//        mockMvc.perform(post("/api/product/save")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)));
//
//        verify(productService, times(1)).saveProduct(any(ProductRequest.class));
//    }
//
//    @Test
//    @DisplayName("Should return 403 when non-admin tries to create product")
//    void testSaveProduct_NotAdmin() throws Exception {
//        // DONE BY COPILOT: Test authorization for product creation
//        ProductRequest request = new ProductRequest();
//
//        when(authServiceClient.isAdmin()).thenReturn(false);
//
//        mockMvc.perform(post("/api/product/save")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isForbidden());
//    }
//
//    // ==================== GET BRANDS ====================
//
//    @Test
//    @DisplayName("Should return all brands with 200 OK status")
//    void testGetAllBrands_Success() throws Exception {
//        // DONE BY COPILOT: Test GET /api/product/brands endpoint
//        List<Brand> brands = new ArrayList<>();
//        brands.add(testBrand);
//
//        when(productService.getAllBrands()).thenReturn(brands);
//
//        mockMvc.perform(get("/api/product/brands")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].brandName", is("TestBrand")));
//    }
//
//    // ==================== CREATE BRAND ====================
//
//    @Test
//    @DisplayName("Should register brand successfully with 200 OK status")
//    void testRegisterBrand_Success() throws Exception {
//        // DONE BY COPILOT: Test POST /api/product/register/brand endpoint
//        BrandRequestDTO request = new BrandRequestDTO();
//        request.setBrandName("NewBrand");
//        request.setBrandDescription("Brand Description");
//
//        when(productService.registerBrand(any(BrandRequestDTO.class))).thenReturn(testBrand);
//
//        mockMvc.perform(post("/api/product/register/brand")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)));
//
//        verify(productService, times(1)).registerBrand(any(BrandRequestDTO.class));
//    }
//
//    // ==================== GET COLOURS ====================
//
//    @Test
//    @DisplayName("Should return all colours with 200 OK status")
//    void testGetAllColours_Success() throws Exception {
//        // DONE BY COPILOT: Test GET /api/product/colours endpoint
//        List<Colour> colours = new ArrayList<>();
//        colours.add(testColour);
//
//        when(productService.getAllColours()).thenReturn(colours);
//
//        mockMvc.perform(get("/api/product/colours")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].colourName", is("Red")));
//    }
//
//    // ==================== CREATE COLOUR ====================
//
//    @Test
//    @DisplayName("Should add colour successfully with 200 OK status")
//    void testAddColour_Success() throws Exception {
//        // DONE BY COPILOT: Test POST /api/product/colour endpoint
//        ColourRequest request = new ColourRequest();
//        request.setColourName("Blue");
//
//        when(productService.addColour(any(ColourRequest.class))).thenReturn(testColour);
//
//        mockMvc.perform(post("/api/product/colour")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.colourName", is("Red")));
//
//        verify(productService, times(1)).addColour(any(ColourRequest.class));
//    }
//
//    // ==================== UPDATE PRODUCT ====================
//
//    @Test
//    @DisplayName("Should update product successfully with 200 OK status")
//    void testUpdateProduct_Success() throws Exception {
//        // DONE BY COPILOT: Test PUT /api/product/{id} endpoint
//        ProductRequest request = new ProductRequest();
//        request.setName("Updated Product");
//        request.setDescription("Updated Description");
//        request.setBrandId(1L);
//        request.setProductCategoryId(1L);
//        request.setProductItemList(new ArrayList<>());
//        request.setProductAttributeDataList(new ArrayList<>());
//
//        when(authServiceClient.isAdmin()).thenReturn(true);
//        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(testProduct);
//
//        mockMvc.perform(put("/api/product/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)));
//
//        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
//    }
//
//    @Test
//    @DisplayName("Should return 403 when non-admin tries to update product")
//    void testUpdateProduct_NotAdmin() throws Exception {
//        // DONE BY COPILOT: Test authorization for product update
//        ProductRequest request = new ProductRequest();
//
//        when(authServiceClient.isAdmin()).thenReturn(false);
//
//        mockMvc.perform(put("/api/product/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isForbidden());
//    }
//
//    // ==================== DELETE PRODUCT ====================
//
//    @Test
//    @DisplayName("Should delete product successfully with 200 OK status")
//    void testDeleteProduct_Success() throws Exception {
//        // DONE BY COPILOT: Test DELETE /api/product/{id} endpoint
//        when(authServiceClient.isAdmin()).thenReturn(true);
//        doNothing().when(productService).deleteProduct(1L);
//
//        mockMvc.perform(delete("/api/product/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).deleteProduct(1L);
//    }
//
//    @Test
//    @DisplayName("Should return 403 when non-admin tries to delete product")
//    void testDeleteProduct_NotAdmin() throws Exception {
//        // DONE BY COPILOT: Test authorization for product deletion
//        when(authServiceClient.isAdmin()).thenReturn(false);
//
//        mockMvc.perform(delete("/api/product/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    // ==================== PRODUCT CATEGORIES ====================
//
//    @Test
//    @DisplayName("Should create product category successfully")
//    void testAddProductCategory_Success() throws Exception {
//        // DONE BY COPILOT: Test POST /api/product/category/add endpoint
//        ProductCategoryRequest request = new ProductCategoryRequest();
//        request.setCategory("Electronics");
//        request.setCategoryDescription("Electronic Products");
//
//        when(authServiceClient.isAdmin()).thenReturn(true);
//
//        mockMvc.perform(post("/api/product/category/add")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).addProductCategory(any(ProductCategoryRequest.class));
//    }
//
//    @Test
//    @DisplayName("Should update product category successfully")
//    void testUpdateProductCategory_Success() throws Exception {
//        // DONE BY COPILOT: Test PUT /api/product/category/{id} endpoint
//        ProductCategoryRequest request = new ProductCategoryRequest();
//        request.setCategory("Updated Category");
//
//        when(authServiceClient.isAdmin()).thenReturn(true);
//
//        mockMvc.perform(put("/api/product/category/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).updateProductCategory(eq(1L), any(ProductCategoryRequest.class));
//    }
//
//    @Test
//    @DisplayName("Should delete product category successfully")
//    void testDeleteProductCategory_Success() throws Exception {
//        // DONE BY COPILOT: Test DELETE /api/product/category/{id} endpoint
//        when(authServiceClient.isAdmin()).thenReturn(true);
//        doNothing().when(productService).deleteProductCategory(1L);
//
//        mockMvc.perform(delete("/api/product/category/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).deleteProductCategory(1L);
//    }
//
//    // ==================== SIZE CATEGORIES ====================
//
//    @Test
//    @DisplayName("Should get all size categories successfully")
//    void testGetAllSizeCategories_Success() throws Exception {
//        // DONE BY COPILOT: Test GET /api/product/size-categories endpoint
//        mockMvc.perform(get("/api/product/size-categories")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).getAllSizeCategories();
//    }
//
//    // ==================== PRODUCT ITEMS ====================
//
//    @Test
//    @DisplayName("Should update product item successfully")
//    void testUpdateProductItem_Success() throws Exception {
//        // DONE BY COPILOT: Test PUT /api/product/product-item/{id} endpoint
//        when(authServiceClient.isAdmin()).thenReturn(true);
//
//        mockMvc.perform(put("/api/product/product-item/1")
//                .param("originalPrice", "100.0")
//                .param("salePrice", "80.0")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).updateProductItemStock(eq(1L), eq(100.0), eq(80.0));
//    }
//
//    @Test
//    @DisplayName("Should delete product item successfully")
//    void testDeleteProductItem_Success() throws Exception {
//        // DONE BY COPILOT: Test DELETE /api/product/product-item/{id} endpoint
//        when(authServiceClient.isAdmin()).thenReturn(true);
//        doNothing().when(productService).deleteProductItem(1L);
//
//        mockMvc.perform(delete("/api/product/product-item/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(productService, times(1)).deleteProductItem(1L);
//    }
//
//    // ==================== ERROR HANDLING ====================
//
//    @Test
//    @DisplayName("Should return 400 when error occurs during product save")
//    void testSaveProduct_BadRequest() throws Exception {
//        // DONE BY COPILOT: Test error handling on product save
//        ProductRequest request = new ProductRequest();
//
//        when(authServiceClient.isAdmin()).thenReturn(true);
//        when(productService.saveProduct(any(ProductRequest.class)))
//                .thenThrow(new RuntimeException("DB Error"));
//
//        mockMvc.perform(post("/api/product/save")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//
//}
//
