package ua.com.serverhelp.simplemonitoring.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.formLogin().loginPage("/login").permitAll();
        http.logout().permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/apiv1/metric/**").hasAuthority("Metrics"); //TODO delete return all metrics
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/apiv1/metric/collectd/").hasAuthority("Metrics");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/apiv1/metric/exporter/**").hasAuthority("Metrics");
        http.authorizeRequests().antMatchers(HttpMethod.GET,
                "/",
                "/index/**",
                "/metrics/detail",
                "/metrics/detail/**",
                "/metrics",
                "/history",
                "/apiv1/gui/metrics/**",
                "/apiv1/gui/history/**",
                "/apiv1/gui/dashboard/**"
        ).hasAuthority("GUI");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/css/**").permitAll();
        http.authorizeRequests().antMatchers("/js/**").permitAll();
        http.authorizeRequests().antMatchers("/error").permitAll();
        http.authorizeRequests().anyRequest().hasAuthority("Administrator");
    }
}