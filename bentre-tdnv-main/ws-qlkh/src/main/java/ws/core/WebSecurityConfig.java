package ws.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().disable()
        	.csrf().disable()
        	.cors().and() // Chia sẻ dữ liệu với các domain khác (mặc dù có addCorsMappings nhưng vẫn phải bật)
           	.authorizeRequests()
           	.antMatchers("/**").permitAll() // Có nghĩa là request "/" ko cần phải đc xác thực
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Xoá các session
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
    }
    
}
