package com.db.property;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertiesJPARepository extends JpaRepository<Properties, Integer> {

	Properties findByPropertyKey(String propertyKey);

}
