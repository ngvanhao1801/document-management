package com.example.demo.config;

import com.example.demo.service.UserDetailService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
    if (exception instanceof BadCredentialsException) {
      // Xử lý lỗi sai tên người dùng hoặc mật khẩu
      getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
    } else if (exception instanceof InternalAuthenticationServiceException && exception.getCause()
        instanceof UserDetailService.UserAccountLockedException) {
      // Xử lý lỗi tài khoản bị khóa
      getRedirectStrategy().sendRedirect(request, response, "/login?accountLocked=true");
    } else {
      // Xử lý các trường hợp khác
      super.onAuthenticationFailure(request, response, exception);
    }
  }
}