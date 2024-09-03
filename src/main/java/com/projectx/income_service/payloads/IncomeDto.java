package com.projectx.income_service.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeDto {
    private Long id;
    @NotNull(message = "please select income type!!")
    private String incomeType;
    private String incomeDate;
    @NotNull(message = "please enter income amount!!")
    private Double incomeAmount;
    private Double grossSalary;
    private Double tdsAmount;
    private Double pfAmount;
    private Double ptAmount;
}
