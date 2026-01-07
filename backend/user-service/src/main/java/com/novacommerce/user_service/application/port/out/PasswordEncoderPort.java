package com.novacommerce.user_service.application.port.out;

/**
 * Puerto de salida para encriptación de contraseñas.
 */
public interface PasswordEncoderPort {

    /**
     * Encripta una contraseña.
     * 
     * @param rawPassword contraseña en texto plano
     * @return contraseña encriptada
     */
    String encode(String rawPassword);

    /**
     * Verifica si una contraseña coincide con su versión encriptada.
     * 
     * @param rawPassword contraseña en texto plano
     * @param encodedPassword contraseña encriptada
     * @return true si coinciden, false en caso contrario
     */
    boolean matches(String rawPassword, String encodedPassword);
}
