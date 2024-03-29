package ru.vasin.animalchipization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import ru.vasin.animalchipization.model.Account;
import ru.vasin.animalchipization.model.Role;
import ru.vasin.animalchipization.service.AccountServiceImpl;
import ru.vasin.web.dto.AccountCreateRequest;
import ru.vasin.web.dto.AccountResponse;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* TODO Тест не обрабатывает проверку на пустую строку в полях Account, потому что
    OpenAPI генератор не поддерживает данную проверку. Надо создать кастомную аннотацию.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
//@Transactional
@ExtendWith(SpringExtension.class)
public class AccountControllerTest {

    @MockBean
    private AccountServiceImpl accountService;

//    @MockBean
//    private AccountRepository accountRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    WebApplicationContext webApplicationContext;
//
//    @Before
//    public void setup() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(webApplicationContext)
//                .apply(springSecurity())
//                .build();
//    }

    @Test
    public void whenPostRequestToAccountsAndValidAccount_thenCorrectResponse() throws Exception {

        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setFirstName("Bob");
        accountCreateRequest.setLastName("Dylan");
        accountCreateRequest.setEmail("bob@domain.com");
        accountCreateRequest.setPassword("bob123");

        when(accountService.createAccount(accountCreateRequest)).thenReturn(AccountResponse.builder()
                .id(1)
                .firstName(accountCreateRequest.getFirstName())
                .lastName(accountCreateRequest.getLastName())
                .email(accountCreateRequest.getEmail())
                .build());

        mockMvc.perform(post("/registration")
                    .content(objectMapper.writeValueAsString(accountCreateRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Dylan"))
                .andExpect(jsonPath("$.email").value("bob@domain.com"));
    }

    @Test
    public void whenPostRequestToAccountsAndInvalidAccount_thenCorrectResponse() throws Exception {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setLastName("Dylan");
        accountCreateRequest.setEmail("bobmail.com");
        accountCreateRequest.setPassword("");

        when(accountService.createAccount(accountCreateRequest)).thenReturn(AccountResponse.builder()
                .id(1)
                .firstName(accountCreateRequest.getFirstName())
                .lastName(accountCreateRequest.getLastName())
                .email(accountCreateRequest.getEmail())
                .build());

        mockMvc.perform(post("/registration")
                        .content(objectMapper.writeValueAsString(accountCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").value("must not be null"))
                .andExpect(jsonPath("$.password").value("size must be between 1 and 255"))
                .andExpect(jsonPath("$.email").value("must be a well-formed email address"));
    }

    @Test
    public void whenPostRequestToAccountsAndExistsEmail_thenCorrectResponse() throws Exception {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setFirstName("Bob");
        accountCreateRequest.setLastName("Dylan");
        accountCreateRequest.setEmail("bob@domain.com");
        accountCreateRequest.setPassword("bob123");

        Account account = new Account(1, "Bob", "Dylan",
                "bob@domain.com", "bob123", Role.ROLE_USER );

        when(accountService.findAccountByEmail(anyString())).thenReturn(Optional.of(account));

        mockMvc.perform(post("/registration")
                        .content(objectMapper.writeValueAsString(accountCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Dylan"))
                .andExpect(jsonPath("$.email").value("bob@domain.com"));
    }

    @Test
    public void whenGetRequestToAccountsAndValidIdAccount_thenCorrectResponse() throws Exception {
        Optional<Account> account = Optional.of(new Account(1, "Bob", "Dylan",
                "bob@domain.com", "bob123", Role.ROLE_USER));

        when(accountService.findAccountById(anyInt())).thenReturn(account);

        mockMvc.perform(get("/accounts/1")
           //     .with(accountHttpBasic(account)))
                .with(httpBasic("bob@domain.com", "bob123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Dylan"))
                .andExpect(jsonPath("$.email").value("bob@domain.com"));
    }

    public RequestPostProcessor accountHttpBasic(Optional<Account> account) {
        return httpBasic(account.get().getEmail(), account.get().getPassword());
    }

//    private Account createTestAccount(AccountCreateRequest account) {
//        Account temp = new Account();
//        temp.setFirstName(account.getFirstName());
//        temp.setLastName(account.getLastName());
//        temp.setEmail(account.getEmail());
//        temp.setPassword(account.getPassword());
//        return accountRepository.save(temp);
//    }
}