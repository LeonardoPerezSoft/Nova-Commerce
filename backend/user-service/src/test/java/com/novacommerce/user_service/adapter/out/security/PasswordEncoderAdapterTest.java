package com.novacommerce.user_service.adapter.out.security;

import com.novacommerce.user_service.application.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordEncoderAdapter Tests")
class PasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderAdapter adapter;

    private static final String RAW_PASSWORD = "myPassword123!";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPasswordHash";

    @BeforeEach
    void setUp() {
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("Should encode raw password")
    void testEncode() {
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        String result = adapter.encode(RAW_PASSWORD);

        assertNotNull(result);
        assertEquals(ENCODED_PASSWORD, result);
        verify(passwordEncoder, times(1)).encode(RAW_PASSWORD);
    }

    @Test
    @DisplayName("Should return non-null encoded password")
    void testEncodeNotNull() {
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        String result = adapter.encode("password");

        assertNotNull(result);
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    @DisplayName("Should match raw password with encoded password")
    void testMatchesTrue() {
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        boolean matches = adapter.matches(RAW_PASSWORD, ENCODED_PASSWORD);

        assertTrue(matches);
        verify(passwordEncoder, times(1)).matches(RAW_PASSWORD, ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("Should return false when passwords do not match")
    void testMatchesFalse() {
        when(passwordEncoder.matches("wrongPassword", ENCODED_PASSWORD)).thenReturn(false);

        boolean matches = adapter.matches("wrongPassword", ENCODED_PASSWORD);

        assertFalse(matches);
        verify(passwordEncoder, times(1)).matches("wrongPassword", ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("Should delegate encoding to PasswordEncoder")
    void testDelegation() {
        when(passwordEncoder.encode("test")).thenReturn("encoded_test");

        adapter.encode("test");

        verify(passwordEncoder, times(1)).encode("test");
    }

    @Test
    @DisplayName("Should handle multiple encode calls")
    void testMultipleEncodeCalls() {
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        String result1 = adapter.encode("password1");
        String result2 = adapter.encode("password2");

        assertNotNull(result1);
        assertNotNull(result2);
        verify(passwordEncoder, times(2)).encode(anyString());
    }

    @Test
    @DisplayName("Should handle multiple matches calls")
    void testMultipleMatchesCalls() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true).thenReturn(false);

        boolean result1 = adapter.matches("pass1", "encoded1");
        boolean result2 = adapter.matches("pass2", "encoded2");

        assertTrue(result1);
        assertFalse(result2);
        verify(passwordEncoder, times(2)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should implement PasswordEncoderPort interface")
    void testImplementsInterface() {
        assertNotNull(adapter);
        assertTrue(adapter instanceof PasswordEncoderPort);
    }
}
