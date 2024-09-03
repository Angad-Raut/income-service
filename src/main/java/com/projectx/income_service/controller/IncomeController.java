package com.projectx.income_service.controller;

import com.projectx.income_service.exceptions.AlreadyExistsException;
import com.projectx.income_service.exceptions.InvalidDataException;
import com.projectx.income_service.exceptions.ResourceNotFoundException;
import com.projectx.income_service.payloads.*;
import com.projectx.income_service.services.IncomeService;
import com.projectx.income_service.utils.ErrorHandlerComponent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(value = "/incomes")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ErrorHandlerComponent errorHandler;

    @PostMapping(value = "/addUpdateIncome")
    public ResponseEntity<ResponseDto<Boolean>> addUpdateIncome(
            @RequestBody @Valid IncomeDto incomeDto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = incomeService.addUpdateIncome(incomeDto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null), HttpStatus.CREATED);
        } catch (AlreadyExistsException | InvalidDataException | ParseException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/getById")
    public ResponseEntity<ResponseDto<IncomeDto>> getById(
            @RequestBody @Valid EntityIdDto entityIdDto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            IncomeDto data = incomeService.getById(entityIdDto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null), HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/getIncomeTypesDropDown")
    public ResponseEntity<ResponseDto<List<EntityNameAndValueDto>>> getIncomeTypesDropDown() {
        try {
            List<EntityNameAndValueDto> result = incomeService.getIncomeTypesDropDown();
            return new ResponseEntity<>(new ResponseDto<>(result,null,null), HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.handleError(e);
        }
    }

    @PostMapping(value = "/updateIncomeStatusById")
    public ResponseEntity<ResponseDto<Boolean>> updateIncomeStatusById(
            @RequestBody @Valid EntityIdDto entityIdDto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = incomeService.updateStatus(entityIdDto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null), HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/deleteIncomeById")
    public ResponseEntity<ResponseDto<Boolean>> deleteIncomeById(
            @RequestBody @Valid EntityIdDto entityIdDto,BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = incomeService.deleteById(entityIdDto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null), HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/getAllIncomesPages")
    public ResponseEntity<ResponseDto<PaginitionResponseDto>> getAllIncomesPages(
            @Valid @RequestBody PageRequestDto dto, BindingResult result) {
        try {
            PaginitionResponseDto data = incomeService.getAllIncomes(dto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null), HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.handleError(e);
        }
    }
}
