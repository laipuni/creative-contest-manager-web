package com.example.cpsplatform;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class CertificateController {

    @GetMapping("/certificate")
    public String getCertificate(@RequestParam("username") String userName, Model model){
        model.addAttribute("name",userName);
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("serialNumber", UUID.randomUUID().toString());
        return "/certificate";
    }

}
