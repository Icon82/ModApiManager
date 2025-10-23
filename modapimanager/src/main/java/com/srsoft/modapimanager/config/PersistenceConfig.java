package com.srsoft.modapimanager.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.srsoft.modaccessk01.entity")
public class PersistenceConfig {

}