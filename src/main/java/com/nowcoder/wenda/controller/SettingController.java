package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SettingController {

    @Autowired
    WendaService wendaService;

    @RequestMapping(path = {"/setting"})
    @ResponseBody
    public String setting() {
        return "Setting ok " + wendaService.getMessage(3);
    }
}
