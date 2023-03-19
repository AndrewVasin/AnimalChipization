package ru.vasin.animalchipization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vasin.animalchipization.mapper.AccountMapper;
import ru.vasin.animalchipization.model.Account;
import ru.vasin.animalchipization.repository.AccountRepository;
import ru.vasin.web.dto.AccountCreateRequest;
import ru.vasin.web.dto.AccountResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public Account loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw  new UsernameNotFoundException("User ‘" + username + "’ not found");
        }
        return account;
    }

    public Optional<Account> findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Optional<Account> findAccountById(Integer id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findAccountByUsername(String username) {
        Account account = accountRepository.findByUsername(username);
        return Optional.ofNullable(account);
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
