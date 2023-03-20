package ru.vasin.animalchipization.service;

import ru.vasin.animalchipization.model.Account;
import ru.vasin.web.dto.AccountCreateRequest;
import ru.vasin.web.dto.AccountResponse;

import java.util.Optional;

public interface AccountService {
    Optional<Account> findAccountByEmail(String email);
    AccountResponse createAccount(AccountCreateRequest accountCreateRequest);
    Optional<Account> findAccountById(Integer id);
    Optional<Account> findAccountByUsername(String username);
}
