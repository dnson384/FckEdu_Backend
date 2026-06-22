package com.fckedu.exam_creation.security.infrastructure.filter;

import com.fckedu.exam_creation.common.dto.token.ATPayload;
import com.fckedu.exam_creation.security.infrastructure.provider.JwtTokenProvider;
import com.fckedu.exam_creation.security.infrastructure.service.CustomUserDetailsService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@NullMarked
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        try {
            // 1. Lấy Access Token từ Cookie thay vì Header Bearer
            String jwt = getJwtFromRequest(request);

            // 2. Validate token
            if (jwt != null && tokenProvider.validateAccessToken(jwt)) {

                // 3. Giải mã lấy ATPayload record bạn đã tạo
                ATPayload payload = tokenProvider.getPayloadFromAccessToken(jwt);

                // 4. Load thông tin chi tiết của User (Sử dụng email làm định danh đăng nhập)
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(payload.getEmail());

                // 5. Cấp quyền và nhét vào SecurityContext
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Không thể xác thực người dùng trong Security Context", ex);
        }

        filterChain.doFilter(request, response);
    }

    // Hàm bổ trợ quét Cookie tìm "access_token"
    private @Nullable String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
