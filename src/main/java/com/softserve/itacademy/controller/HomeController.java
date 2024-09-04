package com.softserve.itacademy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        log.info("Received request to get home page");
        return "home";
    }
}
