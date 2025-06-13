package com.nbdeyy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@Slf4j
@EnableCaching // 开启缓存注解功能
public class HospitalAppointmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalAppointmentApplication.class, args);
        log.info("HospitalAppointmentApplication start success!");
    }
}
