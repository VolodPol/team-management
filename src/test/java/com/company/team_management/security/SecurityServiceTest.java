package com.company.team_management.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {
    @Mock
    private HttpServletRequest request;
    private final static String HEADER = "X-API-KEY";
    private static final String CORRECT_TOKEN = "tm07To05ken*";

    @Test
    public void testNoAccessWithNoKeyAtAll() {
        when(request.getHeader(HEADER)).thenReturn(null);
//        assertNull(SecurityService.getAuthentication(request));
    }

    @Test
    public void testNoAccessWithInvalidKey() {
        when(request.getHeader(HEADER)).thenReturn("invalid");
//        assertNull(SecurityService.getAuthentication(request));
    }

    @Test
    public void testSuccessfulAuthentication() {
        when(request.getHeader(HEADER)).thenReturn(CORRECT_TOKEN);
//        ApiAuthentication expected = new ApiAuthentication(CORRECT_TOKEN, AuthorityUtils.NO_AUTHORITIES);
//        assertEquals(expected, SecurityService.getAuthentication(request));
    }
}