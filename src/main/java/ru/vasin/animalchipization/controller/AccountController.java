package ru.vasin.animalchipization.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.vasin.animalchipization.model.Account;
import ru.vasin.animalchipization.service.AccountServiceImpl;
import ru.vasin.web.controller.AccountApi;
import ru.vasin.web.dto.AccountCreateRequest;
import ru.vasin.web.dto.AccountResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final AccountServiceImpl accountService;

    @Transactional
    @Override
    public ResponseEntity<AccountResponse> createNewAccount(@RequestBody AccountCreateRequest accountCreateRequest) {

        // Проверка имейла на существование. Если аккаунт с таким имейлом уже существует - отдаем 409
        if (accountService.findAccountByEmail((accountCreateRequest.getEmail())).isPresent()) {
            log.error("Account with email " + accountCreateRequest.getEmail() + " already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AccountResponse(null,
                            accountCreateRequest.getFirstName(),
                            accountCreateRequest.getLastName(),
                            accountCreateRequest.getEmail()));
        }

        // На этом этапе данные уже валидные, создаем новый аккаунт и отдаем 201
        AccountResponse accountResponse = accountService.createAccount(accountCreateRequest);
        log.info("New account " + accountResponse.getFirstName() + " " + accountResponse.getLastName() + " created");
        return ResponseEntity.created(URI.create("/accounts/" + accountResponse.getId())).body(accountResponse);
    }

    // TODO: implement validation when accountId is null
    @Override
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer accountId) {
        if (accountId <= 0) {
            log.error("Id " + accountId + " is not correct");
            return ResponseEntity.badRequest().build();
        }
        try {
            accountService.deleteAccountById(accountId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Account with Id: " + accountId + " not found");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("Account with Id: " + accountId + " deleted");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<AccountResponse> findAccount(@PathVariable Integer accountId) {
        Optional<Account> account = accountService.findAccountById(accountId);
        if (account.isEmpty()) {
            log.error("Account with Id: " + accountId + " not found");
            return ResponseEntity.notFound().build();
        }
        new AccountResponse();
        AccountResponse accountResponse = AccountResponse.builder()
                .id(account.get().getId())
                .firstName(account.get().getFirstName())
                .lastName(account.get().getLastName())
                .email(account.get().getEmail())
                .build();
        log.info("Account with Id: " + accountId + " found");
        return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

//    @ExceptionHandler(MissingPathVariableException.class)
//    public ResponseEntity<Void> handleMissingPathVariable(MissingPathVariableException ex,
//                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
//        String error = "Path Variable : " + ex.getVariableName() + " is missing";
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }
}
