package com.start.mts.db;

import com.start.mts.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Integer>, JpaSpecificationExecutor<Record> {

    @Query(value = "select distinct ticket_number from mts.records", nativeQuery = true)
    List<String> findDistinctTicketNumbers();

    List<Record> findByTicketNumber(String ticketNumber);

}
