package com.start.mts.db;

import com.start.mts.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Integer>, JpaSpecificationExecutor<Record> {

    @Query(value = "select distinct ticket_number from mts.records", nativeQuery = true)
    List<String> findDistinctTicketNumbers();

    @Query(value = "select distinct type from mts.object_types", nativeQuery = true)
    List<String> findDistinctObjectTypes();

    @Query(value = "select env_name from mts.environments where is_reference_environment is true", nativeQuery = true)
    List<String> findAllReferenceEnvs();

    @Query(value = "select env_name from mts.environments", nativeQuery = true)
    List<String> findAllEnvironments();

    List<Record> findByTicketNumber(String ticketNumber);

}
