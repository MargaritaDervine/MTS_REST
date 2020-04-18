package com.start.mts.web;

import com.start.mts.RecordNotFoundException;
import com.start.mts.RecordService;
import com.start.mts.db.RecordRepository;
import com.start.mts.domain.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("mts")
public class StartPageController {

    @Autowired
    RecordService service;
    @Autowired
    RecordRepository repository;

    private static final Logger logger = LogManager.getLogger(StartPageController.class);

    @GetMapping
    public List<Record> getRecords(@RequestBody(required = false) String filterTicketId,
                                   @RequestBody(required = false) String filterObjectType,
                                   @RequestBody(required = false) String filterObjectName,
                                   @RequestBody(required = false) String filterName,
                                   @RequestBody(required = false) String filterRefEnv) {

        List<Record> records = service.findByCriteria(filterTicketId,
                filterObjectType,
                filterObjectName,
                filterName,
                filterRefEnv);

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Searching by criteria %s; Found records: %d", Arrays.asList(filterTicketId, filterObjectType, filterObjectName, filterName, filterRefEnv).toString(), records.size()));
        }

        return records;
    }

//mapping for search menus
 /*   @GetMapping
    public List<String> getUniqueTicketIds(){
        return service.getExistingTicketNumbers();
    }

    @GetMapping
    public List<ObjectType> getObjectTypes(){
        return service.getSortedObjectTypes();
    }

    @GetMapping
    public Actions[] getActions(){
        return Actions.values();
    }

    @GetMapping
    public List<Environment> getReferenceEnvironments(){
        return service.getSortedRefEnvironments();
    }

    @GetMapping
    public List<Name> getNames(){
        return service.getSortedNames();
    }*/


    @PostMapping
    public Record addNewRecord(@RequestBody Record record) {
        Record recordSaved = repository.save(record);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Successfully added record %s", Arrays.asList(record.getRecordId(), record.getTicketNumber(), record.getObjectName(), record.getAction(),
                    record.getUserName(), record.getReferenceEnvironment(), record.getObjectType()).toString()));
        }
        return recordSaved;
    }

    @PutMapping("/{id}")
    public Record updateRecord(@PathVariable int recId,
                               @RequestBody Record updatedRecord) throws RecordNotFoundException {

        Optional<Record> recordOp = repository.findById(recId);

        if (!recordOp.isPresent()) {
            throw new RecordNotFoundException(recId);
        }

        Record existingRecord = recordOp.get();

        updateRecordDetails(updatedRecord, existingRecord);

        return repository.save(existingRecord);
    }

    private void updateRecordDetails(Record newRecord, Record existingRecord) {
        existingRecord.setAction(newRecord.getAction());
        existingRecord.setEnvironments(newRecord.getEnvironments());
        existingRecord.setObjectName(newRecord.getObjectName());
        existingRecord.setObjectType(newRecord.getObjectType());
        existingRecord.setRecordId(newRecord.getRecordId());
        existingRecord.setReferenceEnvironment(newRecord.getReferenceEnvironment());
        existingRecord.setTicketNumber(newRecord.getTicketNumber());
        existingRecord.setUserName(newRecord.getUserName());
    }


    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable int recordId) throws RecordNotFoundException {
        Optional<Record> recordOp = repository.findById(recordId);
        if (recordOp.isPresent()) {
            repository.delete(recordOp.get());
        } else
            throw new RecordNotFoundException(recordId);
    }

    @GetMapping("/{id}")
    Record findOne(@PathVariable int recordId) throws RecordNotFoundException {
        Optional<Record> recordOp = repository.findById(recordId);
        if (recordOp.isPresent()) {
            return recordOp.get();
        }
        throw new RecordNotFoundException(recordId);
    }
}
