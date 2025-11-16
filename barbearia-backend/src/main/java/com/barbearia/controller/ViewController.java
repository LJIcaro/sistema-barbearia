package com.barbearia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/servicos")
    public String servicos() {
        return "servicos";
    }

    @GetMapping("/disponibilidades")
    public String disponibilidades() {
        return "disponibilidades";
    }

    @GetMapping("/agendamentos")
    public String agendamentos() {
        return "agendamentos";
    }

    @GetMapping("/meus-agendamentos")
    public String meusAgendamentos() {
        return "meus-agendamentos";
    }

    @GetMapping("/bloqueios")
    public String bloqueios() {
        return "bloqueios";
}
}