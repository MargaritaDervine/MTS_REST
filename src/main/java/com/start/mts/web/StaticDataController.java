package com.start.mts.web;

import com.start.mts.ControllerConstants;
import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.NameRepository;
import com.start.mts.db.ObjectTypeRepository;
import com.start.mts.domain.Environment;
import com.start.mts.domain.Name;
import com.start.mts.domain.ObjectType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Controller
public class StaticDataController {

    @Autowired
    NameRepository nameRepository;
    @Autowired
    EnvironmentRepository environmentRepository;
    @Autowired
    ObjectTypeRepository objectTypeRepository;

    private static final Logger logger = LogManager.getLogger(StaticDataController.class);
    public static final String TEMPLATE_STATIC_DATA_PAGE = "staticDataPage";

    @GetMapping(value = "/staticDataPage")
    public String get(Model model) {
        return TEMPLATE_STATIC_DATA_PAGE;
    }

    @PostMapping(value = "/staticDataPage")
    public String addName(@RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "envName", required = false) String envName,
                          @RequestParam(value = "isReference", required = false) boolean isReference,
                          @RequestParam(value = "objectType", required = false) String objectType,
                          Model model) {

        if (StringUtils.isNotEmpty(name)) {
            tryToSaveName(name, model);
        }

        if (StringUtils.isNotEmpty(envName)) {
            tryToSaveEnvironment(envName, isReference, model);
        }

        if (StringUtils.isNotEmpty(objectType)) {
            tryToSaveObjectType(objectType, model);
        }
        return TEMPLATE_STATIC_DATA_PAGE;
    }

    private void tryToSaveObjectType(String objectType, Model model) {
        ObjectType type = objectTypeRepository.save(new ObjectType(objectType));
        if (StringUtils.isNotEmpty(type.getType())) {
            model.addAttribute(ControllerConstants.SUCCESS_TYPE, true);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Object type successfully saved: %s", type));
            }
        } else {
            model.addAttribute(ControllerConstants.SUCCESS_TYPE, false);
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Error saving object type: %s", type));
            }
        }
    }

    private void tryToSaveEnvironment(String envName, boolean isReference, Model model) {
        Environment environment = new Environment(envName, isReference);
        Environment environmentSaved = environmentRepository.save(environment);
        if (isNotEmpty(environmentSaved.getEnvironmentName())) {
            model.addAttribute(ControllerConstants.SUCCESS_ENV, true);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Environment successfully saved: %s, is reference: %b", envName,  isReference));
            }
        } else {
            model.addAttribute(ControllerConstants.SUCCESS_ENV, false);
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Error saving environment: %s, is reference: %b", envName, isReference));
            }
        }
    }

    private void tryToSaveName(@RequestParam(value = "name", required = false) String name, Model model) {
        Name nameSaved = nameRepository.save(new Name(name));
        if (StringUtils.isNotEmpty(nameSaved.getName())) {
            model.addAttribute(ControllerConstants.SUCCESS_NAME, true);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Name successfully saved: %s", name));
            }
        } else {
            model.addAttribute(ControllerConstants.SUCCESS_NAME, false);
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Error saving name: %s", name));
            }
        }
    }
}
