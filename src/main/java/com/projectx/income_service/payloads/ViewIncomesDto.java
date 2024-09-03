package com.projectx.income_service.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewIncomesDto {
    private Integer srNo;
    private Long incomeId;
    private String incomeType;
    private String incomeDate;
    private String incomeAmount;
}
