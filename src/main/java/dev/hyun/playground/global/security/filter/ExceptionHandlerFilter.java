package dev.hyun.playground.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import dev.hyun.playground.global.error.ErrorDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            setErrorResponse(response, CustomErrorCode.FORBIDDEN);
        } catch (AuthenticationException e) {
            setErrorResponse(response, CustomErrorCode.UNAUTHORIZED);
        } catch (CustomException e) {
            setErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            setErrorResponse(response, CustomErrorCode.SERVER_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, CustomErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        ErrorDto errorDto = new ErrorDto(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorDto));
    }
}
