package com.srsoft.modorder25.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class SettingAppResponse {
	private Long id;
   private String chiave;
    private String valore;
    private boolean encrypted;
    private String description;
  private String category;
    
    

}