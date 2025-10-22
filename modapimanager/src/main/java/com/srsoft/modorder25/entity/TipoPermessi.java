package com.srsoft.modorder25.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TipiPermessi")
@Data
public class TipoPermessi {
    @Id
	public String codice;
    @Column(nullable = false)
	public String descrizione;
    @Column(nullable = false)
	public int  valore;
}
