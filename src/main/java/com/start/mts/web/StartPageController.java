package com.start.mts.web;

import com.start.mts.ControllerConstants;
import com.start.mts.ObjectValidator;
import com.start.mts.RecordService;
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

import java.util.Arrays;
import java.util.List;

import static com.start.mts.ControllerConstants.ATTRIBUTE_ERROR;
import static com.start.mts.ControllerConstants.ATTRIBUTE_SUCCESS;

@Controller
public class StartPageController {

    @Autowired
    RecordService service;
    @Autowired
    RecordRepository repository;
    @Autowired
    EnvironmentRepository environmentRepository;
    @Autowired
    NameRepository nameRepository;
    @Autowired
    ObjectValidator validator;

    private static final Logger logger = LogManager.getLogger(StartPageController.class);
    public static final String TEMPLATE_START_PAGE = "startPage";

    @GetMapping(value = "/")
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
            logger.info(String.format("Searching by criteria %s; Found records: %d", Arrays.asList(filterTicketId, filterObjectType, filterObjectName, filterName, filterRefEnv).toString(), records.size()));
        }

        model.addAttribute(ControllerConstants.ATTRIBUTE_RECORDS, records);
        return TEMPLATE_START_PAGE;
    }

    @PostMapping(value = "/")
    public String addNewRecord(Model model,
                               @RequestParam(value = "refEnv") String refEnvStr,
                               @RequestParam(value = "name") String nameStr,
                               @RequestParam(value = "ticketNumber") String ticketNumber,
                               @RequestParam(value = "objectType") String objectType,
                               @RequestParam(value = "objectName") String objectName,
                               @RequestParam(value = "action") String action) {

        if (StringUtils.isEmpty(ticketNumber) || StringUtils.isEmpty(objectName)) {
            setError(model, "All fields must be filled");
            return TEMPLATE_START_PAGE;
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Trying to create new record with details %s", Arrays.asList(ticketNumber, objectName, action, nameStr, refEnvStr, objectType).toString()));
        }

        Record record = service.createNewRecord(ticketNumber, objectName, action, nameStr, refEnvStr, objectType);

        if (validator.isValidObject(record)) {
            tryToSave(model, record);
        } else {
            setError(model, "Not valid object");
            if (logger.isInfoEnabled()) {
                logger.error(String.format("Record not valid %s", Arrays.asList(ticketNumber, objectName, action, nameStr, refEnvStr, objectType).toString()));
            }
        }
        return TEMPLATE_START_PAGE;
    }

    private void tryToSave(Model model, Record record) {
        Record recordSaved = repository.save(record);
        if (recordSaved.getRecordId() != 0) {
            model.addAttribute(ATTRIBUTE_SUCCESS, true);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Successfully added record %s", Arrays.asList(record.getRecordId(), record.getTicketNumber(), record.getObjectName(), record.getAction(),
                        record.getUserName(), record.getReferenceEnvironment(), record.getObjectType()).toString()));
            }
        } else {
            setError(model, "Failed to save");
            if (logger.isInfoEnabled()) {
                logger.error(String.format("Failed to save record %s", Arrays.asList(record.getTicketNumber(), record.getObjectName(), record.getAction(),
                        record.getUserName(), record.getReferenceEnvironment(), record.getObjectType()).toString()));
            }
        }
    }

    void setError(Model model, String errorMsg) {
        model.addAttribute(ATTRIBUTE_ERROR, errorMsg);
        model.addAttribute(ATTRIBUTE_SUCCESS, false);
    }

}
