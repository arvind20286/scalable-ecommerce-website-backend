package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.Entity.ProductItem;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.ProductItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemServiceImpl Unit Tests")
class ItemServiceImplTest {

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("Should update product item stock successfully")
    void testUpdateProductItemStock_Success() {
        ProductItem item = new ProductItem();
        item.setId(1L);
        item.setOriginalPrice(100.0);
        item.setSalePrice(80.0);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(productItemRepository.save(any(ProductItem.class))).thenReturn(item);

        ProductItem result = itemService.updateProductItemStock(1L, 120.0, 90.0);

        assertThat(result).isNotNull();
        assertThat(result.getOriginalPrice()).isEqualTo(120.0);
        assertThat(result.getSalePrice()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("Should delete product item successfully")
    void testDeleteProductItem_Success() {
        ProductItem item = new ProductItem();
        item.setId(1L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productItemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteProductItem(1L);

        verify(productItemRepository, times(1)).delete(item);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to update product item")
    void testUpdateProductItemStock_NotAdmin() {
        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> itemService.updateProductItemStock(1L, 100.0, 80.0))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to delete product item")
    void testDeleteProductItem_NotAdmin() {
        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> itemService.deleteProductItem(1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating non-existent product item")
    void testUpdateProductItemStock_NotFound() {
        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateProductItemStock(999L, 100.0, 80.0))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should throw BadRequestException when deleting non-existent product item")
    void testDeleteProductItem_NotFound() {
        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteProductItem(999L))
                .isInstanceOf(BadRequestException.class);
    }
}
