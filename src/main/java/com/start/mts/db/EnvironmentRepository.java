package com.start.mts.db;

import com.start.mts.domain.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, String> {
    List<Environment> findAllByIsReferenceEnvironment(boolean isReferenceEnvironment);
}
