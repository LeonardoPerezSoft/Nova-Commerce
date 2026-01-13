package com.novacommerce.auth_service.application.port.in;

import com.novacommerce.auth_service.web.api.dto.request.RegisterRequest;
import com.novacommerce.auth_service.web.api.dto.response.RegisterResponse;

/**
 * Puerto de entrada para el caso de uso de registro público de clientes.
 * Permite que un usuario sin autenticación se registre como cliente.
 * 
 * Esta interfaz define el contrato para el registro y es implementada
 * por la capa de aplicación.
 */
public interface RegisterClientUseCase {

    /**
     * Registra un nuevo cliente (User + Customer) sin requerir autenticación.
     * 
     * Flujo:
     * 1. Valida los datos del registro
     * 2. Crea el Usuario en user-service
     * 3. Crea el Cliente en customer-service
     * 4. Retorna información del registro
     * 
     * @param request datos de registro (email, password, nombre, etc.)
     * @return respuesta con userId, customerId y datos de bienvenida
     * @throws IllegalArgumentException si los datos no son válidos
     * @throws RuntimeException si falla la creación en los microservicios
     */
    RegisterResponse register(RegisterRequest request);
}
