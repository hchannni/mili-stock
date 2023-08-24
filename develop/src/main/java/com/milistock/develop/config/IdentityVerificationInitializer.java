package com.milistock.develop.config;

import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.repository.IdentityVerificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityVerificationInitializer {

 /*   @Bean
    public CommandLineRunner initRoles(IdentityVerificationRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) { // role 테이블에 데이터가 없을 경우
                IdentityVerification userRole = new Role();
                userRole.setRoleId(1L);
                userRole.setName("ROLE_USER");

                IdentityVerification adminRole = new Role();
                adminRole.setRoleId(2L);
                adminRole.setName("ROLE_ADMIN");

                roleRepository.save(userRole);
                roleRepository.save(adminRole);
            }
        };
    }*/
}