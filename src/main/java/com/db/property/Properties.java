package com.db.property;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author ketav
 *
 */
@Getter
@Setter
@Entity
@Table(name = "properties")
public class Properties implements Comparable<Object> {

	@Id
	private int id;
	private String propertyKey;
	private String propertyValue;

	@Override
	public int compareTo(Object o) {
		int result = 0;
		if(this == o) result = 0;
		else if(o == null) result = 1;
		else if((result=propertyKey.compareTo(((Properties) o).propertyKey))==0) {
			result = propertyValue.compareTo(((Properties) o).propertyValue);
		}
		return result;
	}
	
}
