package ru.vasin.animalchipization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasin.animalchipization.mapper.AccountMapper;
import ru.vasin.animalchipization.model.Account;
import ru.vasin.animalchipization.repository.AccountRepository;
import ru.vasin.web.dto.AccountCreateRequest;
import ru.vasin.web.dto.AccountResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public Optional<Account> findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Optional<Account> findAccountById(Integer id) {
        return accountRepository.findById(id);
    }

    public AccountResponse createAccount(AccountCreateRequest accountCreateRequest) {
        Account account = new Account();
        account.setFirstName(accountCreateRequest.getFirstName());
        account.setLastName(accountCreateRequest.getLastName());
        account.setEmail(accountCreateRequest.getEmail());
        account.setPassword(accountCreateRequest.getPassword());

        account = accountRepository.save(account);
        return AccountMapper.INSTANCE.toDto(account);
    }

    public void deleteAccountById(Integer accountId) {
        accountRepository.deleteById(accountId);
    }

}
