package com.novacommerce.order_service.application.port.out;

/**
 * Puerto de salida para validación de productos.
 */
public interface ProductValidationPort {
    
    /**
     * Valida que el producto existe y está activo.
     * 
     * @param productId ID del producto
     * @return true si el producto es válido
     */
    boolean isProductValid(Long productId);
    
    /**
     * Obtiene el nombre del producto.
     */
    String getProductName(Long productId);
    
    /**
     * Obtiene el tipo de producto (para descuentos).
     */
    String getProductType(Long productId);
    
    /**
     * Verifica stock disponible.
     */
    boolean hasStock(Long productId, Integer quantity);
}
