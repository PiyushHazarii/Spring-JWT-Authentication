package me.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class AppConfig {

    // spring issi ke help se fetch karega user name and password jab security lagana hoga to
    // ye bean to fetch karega wo
    // ye in memory database use karega
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails userDetails = User.builder()
                .username("Piyush")
                .password(passwordEncoder().encode("Piyush"))
                .build();
        // aur ek user banayenge agar to niche inmemoryuserdetailsmanager mein pass kr skte hai
        // usme var arguments hai to kitna bhi user pass kr skte hia
        return new InMemoryUserDetailsManager(userDetails);
    }


    // to encode the password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
