package com.start.mts.web;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

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

    @RequestMapping(value = "/", method = RequestMethod.GET)
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

        logger.info("Searching by criteria " + Arrays.asList(filterTicketId, filterObjectType, filterObjectName, filterName, filterRefEnv).toString()
                + "; "
                + "Found records: " + records.size());

        model.addAttribute("records", records);
        return "startPage";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String addNewRecord(Model model,
                               @RequestParam(value = "refEnv", required = true) String refEnvStr,
                               @RequestParam(value = "name", required = true) String nameStr,
                               @RequestParam(value = "ticketNumber", required = true) String ticketNumber,
                               @RequestParam(value = "objectType", required = true) String objectType,
                               @RequestParam(value = "objectName", required = true) String objectName,
                               @RequestParam(value = "action", required = true) String action) {

        if (StringUtils.isEmpty(ticketNumber) || StringUtils.isEmpty(objectName)) {
            setError(model, "All fields must be filled");
            return "startPage";
        }

        logger.info("Trying to create new record with details " + Arrays.asList(ticketNumber, objectName, action, nameStr, refEnvStr, objectType).toString());

        Record record = service.createNewRecord(ticketNumber, objectName, action, nameStr, refEnvStr, objectType);

        if (validator.isValidObject(record)) {
            Record recordSaved = repository.save(record);
            if (recordSaved.getRecordId() != 0) {
                model.addAttribute("success", true);
                logger.info("Successfully added record " + Arrays.asList(record.getRecordId(), ticketNumber, objectName, action, nameStr, refEnvStr, objectType).toString());
            } else {
                setError(model, "Failed to save");
                logger.error("Failed to save record " + Arrays.asList(ticketNumber, objectName, action, nameStr, refEnvStr, objectType).toString());
            }
        } else {
            setError(model, "Not valid object");
            logger.error("Record not valid " + Arrays.asList(ticketNumber, objectName, action, nameStr, refEnvStr, objectType).toString());
        }

        return "startPage";
    }


    void setError(Model model, String errorMsg) {
        model.addAttribute("error", errorMsg);
        model.addAttribute("success", false);
    }

}
