package com.milistock.develop.config;

import com.milistock.develop.domain.IdentityVerification;
import com.milistock.develop.repository.IdentityVerificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityVerificationInitializer {

    @Bean
    public CommandLineRunner initRoles(IdentityVerificationRepository identityVerificationRepository) {
        return args -> {
            if (identityVerificationRepository.count() == 0) { // role 테이블에 데이터가 없을 경우
                IdentityVerification dongHa = new IdentityVerification();
                dongHa.setUserNumber(1L);
                dongHa.setName("김동현");
                dongHa.setServiceNumber("22-70014661");
                dongHa.setJob("병사");
                dongHa.setAffiliation("공군");

                IdentityVerification heoChan = new IdentityVerification();
                heoChan.setUserNumber(2L);
                heoChan.setName("허찬");
                heoChan.setServiceNumber("22-70010541");
                heoChan.setJob("간부");
                heoChan.setAffiliation("공군");

                IdentityVerification choDing = new IdentityVerification();
                choDing.setUserNumber(3L);
                choDing.setName("조윤재");
                choDing.setServiceNumber("22-70015971");
                choDing.setJob("군무원");
                choDing.setAffiliation("육군");

                IdentityVerification kimTae = new IdentityVerification();
                kimTae.setUserNumber(1L);
                kimTae.setName("김동현");
                kimTae.setServiceNumber("22-70011097");
                kimTae.setJob("병사");
                kimTae.setAffiliation("해군");


                identityVerificationRepository.save(dongHa);
                identityVerificationRepository.save(heoChan);
                identityVerificationRepository.save(choDing);
                identityVerificationRepository.save(kimTae);
            }
        };
    }
}