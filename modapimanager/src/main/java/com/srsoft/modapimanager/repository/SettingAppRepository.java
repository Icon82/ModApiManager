package com.srsoft.modapimanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srsoft.modapimanager.entity.SettingApp;

@Repository
public interface SettingAppRepository extends JpaRepository<SettingApp, Long> {
    List<SettingApp> findByCategory(String category);
    
    
	@Query("SELECT s FROM SettingApp s WHERE s.chiave= :chiave")
	Optional <SettingApp> findBychiave(@Param("chiave") String chiave);

    }
