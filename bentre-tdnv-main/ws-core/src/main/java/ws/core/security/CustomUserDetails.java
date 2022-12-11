package ws.core.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import ws.core.model.User;

@Data
@AllArgsConstructor
@SuppressWarnings("serial")
public class CustomUserDetails implements UserDetails{
	private User user;

	public CustomUserDetails() {
	
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return user.password;
	}

	@Override
	public String getUsername() {
		return user.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public void setUser(User user) {
		this.user=user;
	}
	
	public User getUser() {
		return this.user;
	}
}
