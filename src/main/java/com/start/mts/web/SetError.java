package com.start.mts.web;

import org.springframework.ui.Model;

import static com.start.mts.ControllerConstants.ATTRIBUTE_ERROR;
import static com.start.mts.ControllerConstants.ATTRIBUTE_SUCCESS;

public interface SetError {
    default  void setError(Model model, String errorMsg) {
        model.addAttribute(ATTRIBUTE_ERROR, errorMsg);
        model.addAttribute(ATTRIBUTE_SUCCESS, false);
    }
}
