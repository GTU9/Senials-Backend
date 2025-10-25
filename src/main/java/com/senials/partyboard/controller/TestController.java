package com.senials.partyboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @GetMapping("/testPage")
    public ModelAndView testPage(){

        ModelAndView model =  new ModelAndView();
        model.setViewName("main");


        return model;
    }

    @GetMapping("/testPage2")
    public ModelAndView testPage2() {

        ModelAndView model =  new ModelAndView();
        model.setViewName("main2");

        return model;
    }
}
