package backend25.blogisovellus;

//import java.beans.Customizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    //url-tasolla määritelty pääsy eri osoitteisiin
    //tällä hetkellä kaikki rest-palvelut julkisia, en saanut postmanin autentikointia toimimaan
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**").permitAll() //tyylitiedostot julkisia kaikille - muuten tyylit ei näy!
/*              .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/posts**").hasAuthority("USER")
                .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyAuthority("USER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/keywords/**").hasAuthority("ADMIN") */
/*                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/postkeywords**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/keywords**").permitAll()  */
                .requestMatchers("/api/**").permitAll()
            
                .requestMatchers(HttpMethod.GET, "/postlist/**").permitAll() //kaikille näkyvä sivu
                .requestMatchers(HttpMethod.GET, "/post/{id}").permitAll() //kaikille näkyvä sivu
                .requestMatchers(HttpMethod.GET, "/postlistKw/**").permitAll() //kaikille näkyvä sivu
                .requestMatchers(HttpMethod.POST, "/addPost").hasAuthority("USER") //vain sisäänkirjautuneet käyttäjät
                .requestMatchers(HttpMethod.GET, "/postlist_username").hasAuthority("USER") //vain sisäänkirjautuneet käyttäjät
                .requestMatchers(HttpMethod.POST, "/editPost").hasAnyAuthority("USER", "ADMIN") //sekä adminit että userit
                .requestMatchers(HttpMethod.POST, "/postlistEdit").hasAuthority("ADMIN") //vain adminit
                .requestMatchers(HttpMethod.DELETE, "postlistEdit").hasAnyAuthority("ADMIN")
                //.requestMatchers("/h2-console/").permitAll()
                .anyRequest().authenticated()) //tekee postmaniin login-toiminnon
                //.httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions
                .disable())) //h2-konsoli ei toimi ilman tätä
                .formLogin(formlogin -> formlogin //springin default-kirjautumissivu
                //.defaultSuccessUrl("/postlistEdit", true) //mihin tullaan onnistuneen kirjautumisen jälkeen
                .successHandler(customSuccessHandler()) //mihin tullaan onnistuneen sisäänkirjautumisen jälkeen 
                                                        //määritelty erillisessä luokassa
                .permitAll())
                //.logout(logout -> logout.permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/postlist")) //mihin tullaan uloskirjauksen jälkeen
                .csrf(csrf -> csrf.disable()); 

        return http.build();

    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

}
