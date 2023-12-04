package com.company.team_management.security;

import com.company.team_management.security.config.SecurityConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecuredController.class)
@Import(SecurityConfig.class)
class CustomAuthenticationEntryPointTest {

    @Autowired
    private MockMvc mvc;
    @Test
    public void testRequestFromAnonymousUser() throws Exception {
        mvc.perform(get("/secured"))
                .andExpectAll(
                        status().is(HttpServletResponse.SC_UNAUTHORIZED),
                        content().string("Anonymous user detected. Please, provide a header with the valid API key token.")
                );
    }

    @Test
    public void testAuthenticatedRequest() throws Exception {
        mvc.perform(get("/secured")
                .header("X-API-KEY", "tm07To05ken*"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Secured content"));
    }
}

@RestController
class SecuredController {
    @GetMapping("/secured")
    @ResponseBody @ResponseStatus(HttpStatus.OK)
    public String securedEndPoint() {
        return "Secured content";
    }
}