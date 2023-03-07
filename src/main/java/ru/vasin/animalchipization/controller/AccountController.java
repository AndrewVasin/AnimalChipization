package ru.vasin.animalchipization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.vasin.animalchipization.service.AccountServiceImpl;
import ru.vasin.web.controller.AccountApi;
import ru.vasin.web.dto.AccountCreateRequest;
import ru.vasin.web.dto.AccountResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final AccountServiceImpl accountServiceImpl;

    @Override
    public ResponseEntity<AccountResponse> createNewAccount(@RequestBody AccountCreateRequest accountCreateRequest) {

        // Проверка имейла на существование. Если аккаунт с таким имейлом уже существует - отдаем 409
        if (accountServiceImpl.findAccountByEmail((accountCreateRequest.getEmail())).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AccountResponse(null,
                            accountCreateRequest.getFirstName(),
                            accountCreateRequest.getLastName(),
                            accountCreateRequest.getEmail()));
        }

        // На этом этапе данные уже валидные, создаем новый аккаунт и отдаем 201
        AccountResponse accountResponse = accountServiceImpl.createAccount(accountCreateRequest);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> deleteAccount(@PathVariable Integer accountId) {
        accountServiceImpl.deleteAccountById(accountId);
        return ResponseEntity.ok().build();
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
}
