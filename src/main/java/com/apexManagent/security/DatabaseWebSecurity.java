package com.apexManagent.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    @Bean
    public UserDetailsManager customUsers(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("select username, password, status from personal where username = ?");
        users.setAuthoritiesByUsernameQuery("SELECT p.username, r.nombre FROM personal p " +
                "INNER JOIN rol r ON p.rol_id = r.id " +
                "WHERE p.username = ?");
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                // aperturar el acceso a los recursos estáticos
                .requestMatchers("/assets/**", "/css/**", "/js/**").permitAll()

                // las vistas públicas no requieren autenticación
                .requestMatchers("/privacy", "/terms", "/Login").permitAll() 

                // Asignar permisos a URLs por ROLES
                .requestMatchers("/").hasAnyAuthority("Administrador", "Tecnico")
                .requestMatchers("/equipo/**").hasAnyAuthority("Administrador", "Tecnico")
                .requestMatchers("/personal/**").hasAnyAuthority("Administrador")
                .requestMatchers("/rol/**").hasAnyAuthority("Administrador")
                .requestMatchers("/ubicacion/**").hasAnyAuthority("Administrador")

                // todas las demás vistas requieren autenticación
                .anyRequest().authenticated());

        http.formLogin(form -> form
                .loginPage("/Login") // Le dice a Spring Security cuál es la URL de tu página de login
                .permitAll()
                .defaultSuccessUrl("/", true) 
                .failureUrl("/Login?error=true") // Redirige a la página de login con un parámetro de error si falla
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/Login?logout") // Redirige a la página de login con un mensaje de "logout"
                .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
