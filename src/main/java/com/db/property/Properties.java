package com.db.property;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "properties")
public class Properties {

	@Id
	private int id;
	private String propertyKey;
	private String propertyValue;
	
}
