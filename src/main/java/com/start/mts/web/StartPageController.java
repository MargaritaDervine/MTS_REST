package com.start.mts.web;

import com.start.mts.ObjectValidator;
import com.start.mts.RecordService;
import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.NameRepository;
import com.start.mts.db.RecordRepository;
import com.start.mts.domain.Actions;
import com.start.mts.domain.Environment;
import com.start.mts.domain.Name;
import com.start.mts.domain.Record;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getRecords(@RequestParam(required = false) String filterTicketId,
                             @RequestParam(required = false) String filterObjectType,
                             @RequestParam(required = false) String filterObjectName,
                             @RequestParam(required = false) String filterName,
                             @RequestParam(required = false) String filterRefEnv,
                             Model model) {

        List<String> existingTickets = service.getExistingTicketNumbers();
        model.addAttribute("existingTickets", existingTickets);

        List<String> validObjectTypes = service.getObjectTypes();
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

        model.addAttribute("records", records);
        return "startPage";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String addNewRecord(Model model,
                               @RequestParam(value = "refEnv", required = true) String refEnv,
                               @RequestParam(value = "name", required = true) String name,
                               @RequestParam(value = "ticketNumber", required = true) String ticketNumber,
                               @RequestParam(value = "objectType", required = true) String objectTypeStr,
                               @RequestParam(value = "objectName", required = true) String objectName,
                               @RequestParam(value = "action", required = true) String action) {

        if (StringUtils.isEmpty(ticketNumber) || StringUtils.isEmpty(objectName)) {
            setError(model, "All fields must be filled");
            return "startPage";
        }

        //this is workaround as there is comma after action and I can't find from where it appears
        if (action.contains(",")) {
            action = action.replace(",", "");
        }

        Record record = new Record(name, refEnv, ticketNumber.toUpperCase(), objectTypeStr, objectName, action, null);
        if (validator.isValidObject(record)) {
            Record recordSaved = repository.save(record);
            if (recordSaved.getRecordId() != 0) {
                model.addAttribute("success", true);
            } else {
                setError(model, "Failed to save");
            }
        } else {
            setError(model, "Not valid object");
        }

        return "staticData";
    }

    void setError(Model model, String errorMsg) {
        model.addAttribute("error", errorMsg);
        model.addAttribute("success", false);
    }

}
