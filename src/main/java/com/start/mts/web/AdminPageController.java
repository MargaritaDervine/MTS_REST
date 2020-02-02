package com.start.mts.web;

import com.start.mts.AppException;
import com.start.mts.RecordService;
import com.start.mts.db.EnvDeployRepository;
import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.NameRepository;
import com.start.mts.db.RecordRepository;
import com.start.mts.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.start.mts.ControllerService.ATTRIBUTE_ERROR;
import static com.start.mts.ControllerService.ATTRIBUTE_SUCCESS;

@Controller
public class AdminPageController {
    @Autowired
    RecordService service;
    @Autowired
    EnvDeployRepository envDeployRepository;
    @Autowired
    EnvironmentRepository environmentRepository;
    @Autowired
    RecordRepository repository;
    @Autowired
    NameRepository nameRepository;

    private static final Logger logger = LogManager.getLogger(AdminPageController.class);
    private static final String TEMPLATE_ADMIN_PAGE = "adminPage";

    @GetMapping(value = "/adminPage")
    public String getRecords(@RequestParam(required = false) String filterTicketId,
                             @RequestParam(required = false) String filterObjectType,
                             @RequestParam(required = false) String filterObjectName,
                             @RequestParam(required = false) String filterName,
                             @RequestParam(required = false) String filterRefEnv,
                             Model model) {

        List<String> existingTickets = service.getExistingTicketNumbers();
        model.addAttribute("existingTickets", existingTickets);

        List<ObjectType> validObjectTypes = service.getObjectTypes();
        model.addAttribute("validObjectTypes", validObjectTypes);

        Actions[] actions = Actions.values();
        model.addAttribute("actions", actions);

        List<Environment> envs = service.getEnvironments();
        model.addAttribute("envs", envs);

        List<Environment> referenceEnvs = environmentRepository.findAllByIsReferenceEnvironment(true);
        model.addAttribute("referenceEnvs", referenceEnvs);

        List<Name> names = nameRepository.findAll();
        model.addAttribute("names", names);

        List<Record> records = service.findByCriteria(filterTicketId,
                filterObjectType,
                filterObjectName,
                filterName,
                filterRefEnv);

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Searching by criteria %s; Found records: %d", Arrays.asList(filterTicketId, filterObjectType, filterObjectName, filterName, filterRefEnv), records.size()));
        }

        model.addAttribute("records", records);
        return TEMPLATE_ADMIN_PAGE;
    }

    @PostMapping(value = "/adminPage")
    public String addNewDeploy(Model model,
                               @RequestParam(value = "env", required = false) String env,
                               @RequestParam(value = "tickets", required = false) String tickets,
                               @RequestParam(value = "date", required = false) String dateStr,
                               @RequestParam(value = "deleteId", required = false) Integer deleteId) {

        if (deleteId != null) {
            repository.deleteById(deleteId);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Record deleted: %d", deleteId));
            }
            return TEMPLATE_ADMIN_PAGE;
        }

        if (StringUtils.isEmpty(env) || StringUtils.isEmpty(tickets)) {
            model.addAttribute(ATTRIBUTE_ERROR, "Missing field values");
            model.addAttribute(ATTRIBUTE_SUCCESS, false);
            return TEMPLATE_ADMIN_PAGE;
        }

        Date date;
        try {
            date = getDate(dateStr);
        } catch (ParseException e) {
            model.addAttribute(ATTRIBUTE_ERROR, "Error parsing date");
            model.addAttribute(ATTRIBUTE_SUCCESS, false);
            logger.error("Error parsing date " + dateStr, e);
            return TEMPLATE_ADMIN_PAGE;
        }

        List<Record> existingRecords = getRecords(tickets);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Trying to deploy records: %s", existingRecords.toString()));
        }

        try {
            deployAll(env, date, existingRecords);
            model.addAttribute(ATTRIBUTE_SUCCESS, true);
        } catch (AppException e) {
            model.addAttribute(ATTRIBUTE_ERROR, e.getMessage());
            model.addAttribute(ATTRIBUTE_SUCCESS, false);
            logger.error("Error deploying records: " + existingRecords.toString(), e);
        }

        return TEMPLATE_ADMIN_PAGE;
    }

    private List<Record> getRecords(String tickets) {
        List<Record> existingRecords;
        if (tickets.contains(",")) {
            existingRecords = service.getRecordsForSeveralTickets(tickets);
        } else {
            existingRecords = repository.findByTicketNumber(tickets.toUpperCase());
        }
        return existingRecords;
    }

    private void deployAll(String env, Date date, List<Record> existingRecords) throws AppException {
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
        return DateFormat.getInstance().parse(dateStr);
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
