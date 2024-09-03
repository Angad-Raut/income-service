package com.projectx.income_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class SalaryDetails {
    @Column(name = "gross_salary")
    private Double grossSalary;
    @Column(name = "tds_amount")
    private Double tdsAmount;
    @Column(name = "pf_amount")
    private Double pfAmount;
    @Column(name = "pt_amount")
    private Double ptAmount;
    @Column(name = "net_salary")
    private Double netSalary;
}
