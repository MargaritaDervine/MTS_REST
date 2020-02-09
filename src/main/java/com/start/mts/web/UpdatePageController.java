package com.start.mts.web;

import com.start.mts.ObjectValidator;
import com.start.mts.RecordService;
import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.NameRepository;
import com.start.mts.db.ObjectTypeRepository;
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
import java.util.Optional;

import static com.start.mts.ControllerConstants.*;

@Controller
public class UpdatePageController implements SetError{

    @Autowired
    RecordService service;
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    ObjectValidator validator;
    @Autowired
    EnvironmentRepository environmentRepository;
    @Autowired
    NameRepository nameRepository;
    @Autowired
    ObjectTypeRepository objectTypeRepository;

    Record originalRecord = null;

    private static final Logger logger = LogManager.getLogger(UpdatePageController.class);
    private static final String TEMPLATE_UPDATE_PAGE = "updatePage";

    @RequestMapping(value = "/updatePage", method = RequestMethod.GET)
    public String getRecords(Model model) {
        model.addAttribute("records", recordRepository.findAll());
        return TEMPLATE_UPDATE_PAGE;
    }

    @RequestMapping(value = "/updatePage", method = RequestMethod.POST)
    public String updateRecord(Model model,
                               @RequestParam(value = "refEnv", required = false) String refEnvStr,
                               @RequestParam(value = "name", required = false) String nameStr,
                               @RequestParam(value = "ticketNumber", required = false) String ticketNumber,
                               @RequestParam(value = "objectType", required = false) String objectType,
                               @RequestParam(value = "objectName", required = false) String objectName,
                               @RequestParam(value = "action", required = false) String action,
                               @RequestParam(value = "updateId", required = false) Integer updateId) {
        model.addAttribute("records", recordRepository.findAll());

        if (updateId != null && originalRecord == null) {
            model.addAttribute("toUpdate", true);

            Optional<Record> optionalRecord = recordRepository.findById(updateId);
            optionalRecord.ifPresent(value -> originalRecord = value);

            model.addAttribute("recordToUpdate", originalRecord);

            List<ObjectType> validObjectTypes = service.getObjectTypes();
            model.addAttribute("validObjectTypes", validObjectTypes);

            Actions[] actions = Actions.values();
            model.addAttribute("actions", actions);

            List<Environment> referenceEnvs = environmentRepository.findAllByIsReferenceEnvironment(true);
            model.addAttribute("referenceEnvs", referenceEnvs);

            List<Name> names = nameRepository.findAll();
            model.addAttribute("names", names);

            return TEMPLATE_UPDATE_PAGE;
        }

        if (originalRecord != null) {

            if (StringUtils.isEmpty(ticketNumber) || StringUtils.isEmpty(objectName)) {
                setError(model, ERROR_MSG_NOT_FILLED);
                return TEMPLATE_UPDATE_PAGE;
            }

            updateRecord(model, ticketNumber, objectName, action, nameStr, refEnvStr, objectType);

            originalRecord = null;
            return TEMPLATE_UPDATE_PAGE;
        }

        return TEMPLATE_UPDATE_PAGE;
    }

    void updateRecord(Model model, String ticketNumber, String objectName, String action, String nameStr, String refEnvStr, String objectType) {
        Record updatedRecord = originalRecord;

        logger.info(String.format("Trying to update record with details %s", originalRecord));

        updatedRecord.setReferenceEnvironment(environmentRepository.getOne(refEnvStr));
        updatedRecord.setUserName(nameRepository.getOne(nameStr));
        updatedRecord.setTicketNumber(ticketNumber);
        updatedRecord.setObjectType(objectTypeRepository.getOne(objectType));
        updatedRecord.setObjectName(objectName);
        updatedRecord.setAction(Actions.valueOf(action));

        if (validator.isValidObject(updatedRecord)) {
            Record recordSaved = recordRepository.save(updatedRecord);
            if (recordSaved.getRecordId() != 0) {
                model.addAttribute(ATTRIBUTE_SUCCESS, true);
                logger.info(String.format("Successfully updated record %s", recordSaved));
            } else {
                setError(model, ERROR_FAILED_TO_SAVE);
                logger.error(String.format("Failed to save record %s", updatedRecord));
            }
        } else {
            setError(model, ERROR_NOT_VALID_OBJECT);
            logger.error(String.format("Record not valid %s", updatedRecord));
        }
    }
}
