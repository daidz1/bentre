package ws.core.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import ws.core.service.JWTService;
import ws.core.service.LogRequestService;
import ws.core.service.UserService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private Logger log = LogManager.getLogger(JwtAuthenticationFilter.class);
	
	@Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private LogRequestService logRequestService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	try {
            // Lấy jwt từ request
            String jwt = jwtService.getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Lấy username từ chuỗi jwt
                String username = tokenProvider.getUsernameFromJWT(jwt);
                
                // Lấy thông tin người dùng từ username
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                // Nếu người dùng hợp lệ, set thông tin cho Seturity Context
                if(userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    /* Ghi log mọi request */
                	try {
                		logRequestService.writeLogRequest(request, jwt);
            		} catch (Exception e) {}
                }else {
                	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tài khoản hoặc mật khẩu không đúng");
                }
            }else {
            	
            }
        } catch (Exception e) {
            log.error("failed on set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}
