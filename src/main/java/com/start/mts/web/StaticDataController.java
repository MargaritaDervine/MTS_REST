package com.start.mts.web;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = "/staticDataPage", method = RequestMethod.GET)
    public String get(Model model) {
        return "staticDataPage";
    }

    @RequestMapping(value = "/staticDataPage", method = RequestMethod.POST)
    public String addName(@RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "envName", required = false) String envName,
                          @RequestParam(value = "isReference", required = false) boolean isReference,
                          @RequestParam(value = "objectType", required = false) String objectType,
                          Model model) {

        if (StringUtils.isNotEmpty(name)) {
            Name nameSaved = nameRepository.save(new Name(name));
            if (StringUtils.isNotEmpty(nameSaved.getName())) {
                model.addAttribute("successName", true);
                logger.info("Name successfully saved: " + name);
            } else {
                model.addAttribute("successName", false);
                logger.error("Error saving name: " + name);
            }
        }

        if (StringUtils.isNotEmpty(envName)) {
            Environment environment = new Environment(envName, isReference);
            Environment environmentSaved = environmentRepository.save(environment);
            if (isNotEmpty(environmentSaved.getEnvironmentName())) {
                model.addAttribute("successEnv", true);
                logger.info("Environment successfully saved: " + envName + " is reference: " + isReference);
            } else {
                model.addAttribute("successEnv", false);
                logger.error("Error saving environment: " + envName + " is reference: " + isReference);
            }
        }

        if (StringUtils.isNotEmpty(objectType)) {
            ObjectType type = objectTypeRepository.save(new ObjectType(objectType));
            if (StringUtils.isNotEmpty(type.getType())) {
                model.addAttribute("successType", true);
                logger.info("Object type successfully saved: " + type);
            } else {
                model.addAttribute("successType", false);
                logger.error("Error saving object type: " + type);
            }
        }

        return "staticDataPage";
    }
}
