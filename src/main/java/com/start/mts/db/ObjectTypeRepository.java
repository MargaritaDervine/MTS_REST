package com.start.mts.db;

import com.start.mts.domain.ObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectTypeRepository extends JpaRepository<ObjectType, String> {

}
