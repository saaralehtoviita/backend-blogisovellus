package backend25.blogisovellus;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectURL = request.getContextPath();

        if(authentication.getAuthorities().stream().anyMatch(a ->
        a.getAuthority().equals("ADMIN"))) {
            redirectURL = "/postlistEdit";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> 
        a.getAuthority().equals("USER"))) {
            String userName = authentication.getName();
            redirectURL = "/postlist_username/" + userName;
        }

        response.sendRedirect(redirectURL);

}
}
