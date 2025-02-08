package com.example.onlinestorespringboot.handler;

import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.i18n.LocalHolder;
import com.example.onlinestorespringboot.util.Messages;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final I18nUtil i18nUtil;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String acceptLanguageHeader = request.getHeader("Accept-Language");
        Locale locale = Locale.forLanguageTag(acceptLanguageHeader != null ? acceptLanguageHeader : "ru");

        i18nUtil.setLocale(locale);

        response.getWriter().write(new ObjectMapper()
                .writeValueAsString(new AppErrorDto(i18nUtil.getMessage(Messages.USER_ERROR_FORBIDDEN), 403)));
    }
}
