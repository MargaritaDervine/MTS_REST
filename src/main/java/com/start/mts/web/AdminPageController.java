package com.start.mts.web;

import com.start.mts.RecordService;
import com.start.mts.db.EnvDeployRepository;
import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.NameRepository;
import com.start.mts.db.RecordRepository;
import com.start.mts.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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


    @RequestMapping(value = "/adminPage", method = RequestMethod.GET)
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

        model.addAttribute("records", records);
        return "adminPage";
    }

    @RequestMapping(value = "/adminPage", method = RequestMethod.POST)
    public String addNewDeploy(Model model,
                               @RequestParam(value = "env", required = false) String env,
                               @RequestParam(value = "tickets", required = false) String tickets,
                               @RequestParam(value = "date", required = false) String dateStr,
                               @RequestParam(value = "deleteId", required = false) Integer deleteId) {

        if (deleteId != null) {
            repository.deleteById(deleteId);
            return "adminPage";
        }

        if (StringUtils.isEmpty(env) || StringUtils.isEmpty(tickets)) {
            model.addAttribute("error", "Missing field values");
            model.addAttribute("success", false);
            return "adminPage";
        }

        Date date;
        try {
            date = getDate(dateStr);
        } catch (ParseException e) {
            model.addAttribute("error", "Error parsing date");
            model.addAttribute("success", false);
            e.printStackTrace();
            return "adminPage";
        }

        List<Record> existingRecords;

        if (tickets.contains(",")) {
            existingRecords = service.getRecordsForSeveralTickets(tickets);
        } else {
            existingRecords = repository.findByTicketNumber(tickets.toUpperCase());
        }

        try {
            deployAll(env, date, existingRecords);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("success", false);
        }

        model.addAttribute("success", true);
        return "adminPage";
    }

    private void deployAll(String env, Date date, List<Record> existingRecords) throws Exception {
        for (Record record : existingRecords) {
            EnvDeploy deploy = new EnvDeploy(environmentRepository.getOne(env), date, record);
            addToRecordAndSave(record, deploy);
        }
    }

    private void addToRecordAndSave(Record record, EnvDeploy deploy) throws Exception {
        List<EnvDeploy> list = record.getEnvironments();
        list.add(deploy);
        record.setEnvironments(list);
        save(deploy);
    }

    private Date getDate(@RequestParam(value = "date", required = false) String dateStr) throws ParseException {
        Date date;
        if (StringUtils.isEmpty(dateStr)) {
            date = new Date();
        } else {
            date = DateFormat.getInstance().parse(dateStr);
        }
        return date;
    }

    private void save(EnvDeploy deploy) throws Exception {
        EnvDeploy saved = envDeployRepository.save(deploy);
        if (saved.getEnvDeployId() == 0) {
            throw new Exception("error saving deploy");
        }
    }

}
