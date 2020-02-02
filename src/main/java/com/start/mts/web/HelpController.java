package com.start.mts.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {
    @GetMapping(value = "/help")
    public String get(Model model) {
        return "help";
    }

}
