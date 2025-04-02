package com.srsoft.modorder25.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Table(name = "app_settings")
public class SettingApp {

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	    
	 
    @Column(nullable = false)
    private String chiave;

    @Column( columnDefinition = "TEXT")
    private String valore;

    @Column(nullable = false)
    private boolean encrypted;

    @Column(name = "initialization_vector")
    private String initializationVector;

    private String description;


    private String category;
}
