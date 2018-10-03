package gov.nsf.psm.documentgeneration.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nsf.psm.foundation.exception.CommonUtilException;

@RestController
public class F5Contoller {

    private static final Logger LOGGER = LoggerFactory.getLogger(F5Contoller.class);

    @RequestMapping(value = "/f5health", method = RequestMethod.GET)
    @ResponseBody
    public String whatIsMyStatus() throws CommonUtilException {
        LOGGER.debug("F5Contoller.whatIsMyStatus");
        return "UP";
    }

}
