package ru.vasin.animalchipization.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vasin.animalchipization.model.Account;
import ru.vasin.web.dto.AccountResponse;

@Mapper(uses = AccountMapper.class)
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountResponse toDto(Account account);
}
