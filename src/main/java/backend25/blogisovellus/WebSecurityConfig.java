package backend25.blogisovellus;

import java.beans.Customizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//URL-tasoiset security oikeudet
@Configuration
//metoditasoiset security-konfigit annotaatio
@EnableMethodSecurity
public class WebSecurityConfig {

    //kun luodaan uusi käyttäjä, tehdään salasanasta kryptattu versio
    @Bean 
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
            authorize -> authorize
            .requestMatchers(HttpMethod.GET, "/postlist").permitAll()
            .requestMatchers(HttpMethod.GET, "/post/{id}").permitAll()
            .requestMatchers(HttpMethod.POST, "/addPost").hasAuthority("USER")
            .requestMatchers(HttpMethod.POST, "/postlistEdit").hasAuthority("ADMIN")
            .requestMatchers("/h2-console/").permitAll()
            .anyRequest().authenticated()) //tekee postmaniin login-toiminnon
            //.httpBasic(Customizer.withDefaults())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions
                .disable())) //h2-konsoli ei toimi ilman tätä
            .formLogin(formlogin -> formlogin //springin default-kirjautumissivu
                .defaultSuccessUrl("/postlistEdit", true) //mihin tullaan onnistuneen kirjautumisen jälkeen
                .permitAll())
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.disable());

        return http.build();

    }

}
