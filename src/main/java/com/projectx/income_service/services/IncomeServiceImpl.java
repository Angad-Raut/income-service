package com.projectx.income_service.services;

import com.projectx.income_service.entity.IncomeDetails;
import com.projectx.income_service.entity.SalaryDetails;
import com.projectx.income_service.exceptions.AlreadyExistsException;
import com.projectx.income_service.exceptions.InvalidDataException;
import com.projectx.income_service.exceptions.ResourceNotFoundException;
import com.projectx.income_service.payloads.*;
import com.projectx.income_service.repository.IncomeRepository;
import com.projectx.income_service.utils.IncomeUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Transactional
    @Override
    public Boolean addUpdateIncome(IncomeDto dto) throws ResourceNotFoundException,
            AlreadyExistsException, InvalidDataException, ParseException {
        IncomeDetails incomeDetails = null;
        if (dto.getId()==null) {
            incomeDetails = setIncomes(dto);
        } else {
            IncomeDetails details = incomeRepository.getById(dto.getId());
            incomeDetails = updateIncome(details,dto);
        }
        try {
             return incomeRepository.save(incomeDetails)!=null?true:false;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (AlreadyExistsException e) {
            throw new AlreadyExistsException(e.getMessage());
        } catch (RuntimeException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    @Override
    public IncomeDto getById(EntityIdDto dto) throws ResourceNotFoundException {
        try {
            IncomeDetails details = incomeRepository.getById(dto.getEntityId());
            if (details==null) {
                throw new ResourceNotFoundException(IncomeUtils.INCOME_DETAILS_NOT_EXISTS);
            }
            return details.getSalaryDetails()!=null?(IncomeDto.builder()
                            .id(details.getId())
                            .incomeType(details.getIncomeType())
                            .incomeAmount(details.getIncomeAmount())
                            .incomeDate(IncomeUtils.toExpenseDate(details.getIncomeDate()))
                            .grossSalary(details.getSalaryDetails().getGrossSalary())
                            .pfAmount(details.getSalaryDetails().getPfAmount())
                            .ptAmount(details.getSalaryDetails().getPtAmount())
                            .tdsAmount(details.getSalaryDetails().getTdsAmount())
                       .build()
            ): (IncomeDto.builder()
                        .id(details.getId())
                        .incomeDate(IncomeUtils.toExpenseDate(details.getIncomeDate()))
                        .incomeType(details.getIncomeType())
                        .incomeAmount(details.getIncomeAmount())
                    .build());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public List<EntityNameAndValueDto> getIncomeTypesDropDown() {
        List<EntityNameAndValueDto> responseList = new ArrayList<>();
        responseList.add(new EntityNameAndValueDto(IncomeUtils.SALARY_TYPE,"Salary"));
        responseList.add(new EntityNameAndValueDto(IncomeUtils.OTHER_TYPE,"Other"));
        return responseList;
    }

    @Override
    public Boolean updateStatus(EntityIdDto dto) throws ResourceNotFoundException {
        try {
            IncomeDetails details = incomeRepository.getById(dto.getEntityId());
            if (details==null) {
                throw new ResourceNotFoundException(IncomeUtils.INCOME_DETAILS_NOT_EXISTS);
            }
            Boolean status = details.getStatus()?false:true;
            Integer count = incomeRepository.updateStatus(dto.getEntityId(),status);
            return count==1?true:false;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public PaginitionResponseDto getAllIncomes(PageRequestDto dto) {
        String sortParameter = "";
        if (dto.getSortParam()!=null && dto.getSortParam().equals("srNo")) {
            sortParameter = "id";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("incomeType")) {
            sortParameter = "income_type";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("incomeDate")) {
            sortParameter = "income_date";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("incomeAmount")) {
            sortParameter = "income_amount";
        } else {
            sortParameter = "income_date";
        }
        Sort sort = dto.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortParameter).ascending()
                : Sort.by(sortParameter).descending();
        Pageable pageable = PageRequest.of(dto.getPageNumber()-1, dto.getPageSize(), sort);
        Page<IncomeDetails> incomes = incomeRepository.getAllIncomesPages(pageable);
        Integer pageNumber = dto.getPageNumber()-1;
        AtomicInteger index = new AtomicInteger(dto.getPageSize()*pageNumber);
        List<IncomeDetails> listOfIncomes = incomes.getContent();
        List<ViewIncomesDto> incomesList = !listOfIncomes.isEmpty()?listOfIncomes.stream()
                .map(data -> ViewIncomesDto.builder()
                        .srNo(index.incrementAndGet())
                        .incomeId(data.getId())
                        .incomeType(data.getIncomeType().equals(IncomeUtils.SALARY_TYPE)?"Salary":"Other")
                        .incomeAmount(IncomeUtils.toINRFormat(data.getIncomeAmount()))
                        .incomeDate(IncomeUtils.toExpenseDate(data.getIncomeDate()))
                        .build()).toList()
                :new ArrayList<>();
        return !incomesList.isEmpty()?PaginitionResponseDto.builder()
                .pageNo(incomes.getNumber())
                .pageSize(incomes.getSize())
                .totalPages(incomes.getTotalPages())
                .totalElements(incomes.getTotalElements())
                .content(incomesList)
                .build():new PaginitionResponseDto();
    }

    @Override
    public Boolean deleteById(EntityIdDto dto) throws ResourceNotFoundException {
        try {
            IncomeDetails details = incomeRepository.getById(dto.getEntityId());
            if (details==null) {
                throw new ResourceNotFoundException(IncomeUtils.INCOME_DETAILS_NOT_EXISTS);
            }
            incomeRepository.deleteById(dto.getEntityId());
            return true;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public Integer getIncomeCount() {
        Integer count = incomeRepository.getIncomeCount();
        return count!=null?count:0;
    }

    @Override
    public String getIncomeSum() {
        Double incomeSum = incomeRepository.getTotalIncomeSum();
        return incomeSum!=null?IncomeUtils.toINRFormat(incomeSum):IncomeUtils.toINRFormat(0.0);
    }

    private IncomeDetails setIncomes(IncomeDto dto) throws ParseException {
        if (dto.getIncomeType().equals(IncomeUtils.SALARY_TYPE)) {
            Double deductedAmount = (dto.getTdsAmount()+dto.getPfAmount()+dto.getPtAmount());
            Double netSalary = (dto.getGrossSalary()-deductedAmount);
            return IncomeDetails.builder()
                    .incomeDate(dto.getIncomeDate()!=null?IncomeUtils.getISODate(dto.getIncomeDate()):new Date())
                    .incomeType(dto.getIncomeType())
                    .incomeAmount(dto.getGrossSalary())
                    .salaryDetails(SalaryDetails.builder()
                            .grossSalary(dto.getGrossSalary())
                            .pfAmount(dto.getPfAmount())
                            .ptAmount(dto.getPtAmount())
                            .tdsAmount(dto.getTdsAmount())
                            .netSalary(netSalary)
                            .build())
                    .status(true)
                    .build();
        } else {
            return IncomeDetails.builder()
                    .incomeDate(dto.getIncomeDate()!=null?IncomeUtils.getISODate(dto.getIncomeDate()):new Date())
                    .incomeType(dto.getIncomeType())
                    .incomeAmount(dto.getIncomeAmount())
                    .status(true)
                    .build();
        }
    }
    private IncomeDetails updateIncome(IncomeDetails details,IncomeDto dto) throws ParseException {
        if (!dto.getIncomeType().equals(details.getIncomeType())) {
            throw new InvalidDataException(IncomeUtils.INCOME_TYPE_NOT_CHANGED);
        }
        if (dto.getIncomeType().equals(IncomeUtils.SALARY_TYPE) && details.getSalaryDetails()!=null) {
            SalaryDetails salaryDetails = details.getSalaryDetails();
            Double deductedAmount = (dto.getTdsAmount()+dto.getPfAmount()+dto.getPtAmount());
            Double netSalary = (dto.getGrossSalary()-deductedAmount);
            if (!dto.getGrossSalary().equals(salaryDetails.getGrossSalary())) {
                details.setIncomeAmount(dto.getGrossSalary());
                salaryDetails.setGrossSalary(dto.getGrossSalary());
            }
            if (!dto.getTdsAmount().equals(salaryDetails.getTdsAmount())) {
                salaryDetails.setTdsAmount(dto.getTdsAmount());
            }
            if (!dto.getPtAmount().equals(salaryDetails.getPtAmount())) {
                salaryDetails.setPtAmount(dto.getPtAmount());
            }
            if (!dto.getPfAmount().equals(salaryDetails.getPfAmount())) {
                salaryDetails.setPfAmount(dto.getPfAmount());
            }
            if (!netSalary.equals(salaryDetails.getNetSalary())) {
                salaryDetails.setNetSalary(netSalary);
            }
            details.setSalaryDetails(salaryDetails);
        } else if (dto.getIncomeType().equals(IncomeUtils.OTHER_TYPE) && details.getSalaryDetails()==null) {
            if (!dto.getIncomeAmount().equals(details.getIncomeAmount())) {
                details.setIncomeAmount(dto.getIncomeAmount());
            }
        }
        return details;
    }
}
