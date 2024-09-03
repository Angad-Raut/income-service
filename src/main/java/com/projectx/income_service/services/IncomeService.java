package com.projectx.income_service.services;

import com.projectx.income_service.exceptions.AlreadyExistsException;
import com.projectx.income_service.exceptions.InvalidDataException;
import com.projectx.income_service.exceptions.ResourceNotFoundException;
import com.projectx.income_service.payloads.*;

import java.text.ParseException;
import java.util.List;

public interface IncomeService {
    Boolean addUpdateIncome(IncomeDto dto) throws ResourceNotFoundException,
            AlreadyExistsException, InvalidDataException, ParseException;
    IncomeDto getById(EntityIdDto dto)throws ResourceNotFoundException;
    List<EntityNameAndValueDto> getIncomeTypesDropDown();
    Boolean updateStatus(EntityIdDto dto)throws ResourceNotFoundException;
    PaginitionResponseDto getAllIncomes(PageRequestDto dto);
    Boolean deleteById(EntityIdDto dto)throws ResourceNotFoundException;
    Integer getIncomeCount();
    String getIncomeSum();
}
