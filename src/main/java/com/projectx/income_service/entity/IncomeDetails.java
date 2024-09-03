package com.projectx.income_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "income_details")
public class IncomeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "income_type")
    private String incomeType;
    @Column(name = "income_date")
    private Date incomeDate;
    @Column(name = "income_amount")
    private Double incomeAmount;
    @Column(name = "status")
    private Boolean status;
    @Embedded
    private SalaryDetails salaryDetails;
}
