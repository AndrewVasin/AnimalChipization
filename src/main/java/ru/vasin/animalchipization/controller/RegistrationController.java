package ru.vasin.animalchipization.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vasin.animalchipization.repository.AccountRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    public RegistrationController(
            AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping
    public String registerForm() {
        return "registration";
    }
    @PostMapping
    public String processRegistration(RegistrationForm form) {
        accountRepository.save(form.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
