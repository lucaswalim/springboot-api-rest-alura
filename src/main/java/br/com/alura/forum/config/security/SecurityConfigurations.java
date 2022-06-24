package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity // Habilita modulo de segurança
@Configuration // Ao start do projeto Spring ira ler a classe com anotação @Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

    @Autowired
    // Classe criada para validar autenticação do usuario padrão Spring
    private AutenticacaoService autenticacaoService;

    // Configurações a partir de Autenticação, login
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
    }

    // Configuração a partir de Autorização, quem pode acessar tal url
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/topicos").permitAll()
                .antMatchers(HttpMethod.GET, "/topicos/*").permitAll() //topicos/{id}
                .anyRequest().authenticated()
                .and().formLogin(); // Retorna uma página de login feita pelo Spring
    }

    // Configurações de recursos estáticos (js, css, imagens)
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
/*
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("132456"));
    }
 */
}
