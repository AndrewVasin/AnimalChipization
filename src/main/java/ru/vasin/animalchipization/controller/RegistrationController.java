package ru.vasin.animalchipization.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vasin.animalchipization.repository.AccountRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private AccountRepository accountRepository;
    //private PasswordEncoder passwordEncoder;
    public RegistrationController(
            AccountRepository accountRepository){//, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
       // this.passwordEncoder = passwordEncoder;
    }

}
