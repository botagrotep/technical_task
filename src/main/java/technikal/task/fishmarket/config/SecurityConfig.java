package technikal.task.fishmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.formLogin(form -> form.permitAll()).logout(logout -> logout.permitAll());

		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService(PasswordEncoder encoder) {

		UserDetails admin = User.builder().username("admin").password(encoder.encode("admin")).roles("ADMIN").build();

		UserDetails user = User.builder().username("user").password(encoder.encode("user")).roles("USER").build();

		return new InMemoryUserDetailsManager(admin, user);
	}

}
