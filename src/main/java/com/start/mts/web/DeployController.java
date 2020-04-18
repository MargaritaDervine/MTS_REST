package com.start.mts.web;

import com.start.mts.AppException;
import com.start.mts.RecordNotFoundException;
import com.start.mts.RecordService;
import com.start.mts.db.EnvDeployRepository;
import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.RecordRepository;
import com.start.mts.domain.EnvDeploy;
import com.start.mts.domain.Record;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.start.mts.RecordService.COMMA;
import static com.start.mts.RecordService.EMPTY;

@RestController
@RequestMapping("mts/deploy")
public class DeployController {
    @Autowired
    RecordService service;
    @Autowired
    EnvDeployRepository envDeployRepository;
    @Autowired
    EnvironmentRepository environmentRepository;
    @Autowired
    RecordRepository repository;

    private static final Logger logger = LogManager.getLogger(DeployController.class);

    @PostMapping
    public void addNewDeploy(@RequestParam(required = false) String env,
                             @RequestParam(required = false) String tickets,
                             @RequestParam(required = false) String dateStr) throws RecordNotFoundException, AppException {

        Date date;
        try {
            date = getDate(dateStr);
        } catch (ParseException e) {
            logger.error("Error parsing date " + dateStr, e);
            throw new AppException(String.format("Date cannot be parsed %s", dateStr));
        }

        List<Record> existingRecords = getRecords(tickets);

        if (existingRecords.isEmpty()) {
            throw new RecordNotFoundException(String.format("No records found for ids: %s", tickets));
        }

        deployAll(env, date, existingRecords);
    }

    private List<Record> getRecords(String tickets) {
        List<Record> existingRecords;
        if (tickets.contains(COMMA)) {
            existingRecords = service.getRecordsForSeveralTickets(tickets, COMMA);
        } else if (StringUtils.trim(tickets).contains(EMPTY)) {
            existingRecords = service.getRecordsForSeveralTickets(tickets, EMPTY);
        } else {
            existingRecords = repository.findByTicketNumber(tickets.toUpperCase());
        }
        return existingRecords;
    }

    private void deployAll(String env, Date date, List<Record> existingRecords) throws AppException {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Trying to deploy records: %s", existingRecords.toString()));
        }

        for (Record record : existingRecords) {
            EnvDeploy deploy = new EnvDeploy(environmentRepository.getOne(env), date, record);
            addToEnvironmentsToRecordAndSave(record, deploy);
        }
    }

    private void addToEnvironmentsToRecordAndSave(Record record, EnvDeploy deploy) throws AppException {
        List<EnvDeploy> list = record.getEnvironments();
        list.add(deploy);
        record.setEnvironments(list);
        save(deploy);
    }

    private Date getDate(String dateStr) throws ParseException {
        Date date;
        if (StringUtils.isEmpty(dateStr)) {
            date = getNow();
        } else {
            date = parseDate(dateStr);
        }
        return date;
    }

    private Date parseDate(String dateStr) throws ParseException {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Parsing date: %s", dateStr));
        }
        return new SimpleDateFormat("yyyy-MM-DD'T'hh:mm").parse(dateStr);
    }

    private Date getNow() {
        Date date = new Date();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Date and time is empty, taking current date: %s", date));
        }
        return date;
    }

    private void save(EnvDeploy deploy) throws AppException {
        EnvDeploy saved = envDeployRepository.save(deploy);
        if (saved.getEnvDeployId() == 0) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Error saving deploy: %s", deploy));
            }
            throw new AppException("Error saving deploy");
        }
    }

}
