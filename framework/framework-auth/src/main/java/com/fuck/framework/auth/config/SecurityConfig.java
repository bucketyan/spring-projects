package com.fuck.framework.auth.config;

import com.fuck.framework.auth.security.CustomLogoutHandler;
import com.fuck.framework.auth.security.RemoteAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

/**
 * DESCRIPTION:
 * 配置AuthenticationManager
 * 添加自定义AuthenticationProvider
 * 便于自身业务login的验证
 * AuthenticationProcessingFilter --> AuthenticationManager(ProviderManager...) --> AuthenticationProvider
 * 例：
 * org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 * @author zouyan
 * @create 2017-12-21 19:34
 * Created by fuck~ on 2017/12/21.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RemoteAuthenticationProvider remoteAuthenticationProvider;
    /**
     * AuthenticationManager --> ProviderManager
     * --> List<AuthenticationProvider> --> AbstractAuthenticationToken
     * 不同provider对应不同验证方式
     * 添加自定义provider（使用包含自身业务的验证）
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(remoteAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // .antMatchers("/resources/**", "/signup", "/about").permitAll()//放行
                // .antMatchers("/admin/**").hasRole("ADMIN")//admin路径需admin角色
                // .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated() //未匹配的进行用户身份校验
                .and().logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .addLogoutHandler(customLogoutHandler());

        /*http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers().antMatchers("/**")
                .and().authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .addLogoutHandler(customLogoutHandler());*/
        /*
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers().antMatchers("/**")
                .and().authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();
        */
    }

    @Bean
    public CustomLogoutHandler customLogoutHandler() {
        return new CustomLogoutHandler();
    }
}
