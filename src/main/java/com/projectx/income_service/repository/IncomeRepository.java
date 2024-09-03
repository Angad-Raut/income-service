package com.projectx.income_service.repository;

import com.projectx.income_service.entity.IncomeDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeDetails,Long> {
    @Query(value = "select * from income_details where id=:incomeId",nativeQuery = true)
    IncomeDetails getById(@Param("incomeId")Long incomeId);

    @Transactional
    @Modifying
    @Query(value = "update income_details status=:status where id=:incomeId",nativeQuery = true)
    Integer updateStatus(@Param("incomeId")Long incomeId,@Param("status")Boolean status);

    @Query(value = "select count(*) from income_details",nativeQuery = true)
    Integer getIncomeCount();

    @Query(value = "select sum(income_amount) from income_details "
            +"where income_date between :startDate and :endDate",nativeQuery = true)
    Double getTotalIncomeAmountWithDateRange(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    @Query(value = "select sum(income_amount) from income_details",nativeQuery = true)
    Double getTotalIncomeSum();

    @Query(value = "select * from income_details "
            +"where income_date between :startDate and :endDate",nativeQuery = true)
    List<IncomeDetails> getAllIncomesWithDateRange(@Param("startDate")Date startDate,@Param("endDate")Date endDate);

    @Query(value = "select * from income_details",nativeQuery = true)
    Page<IncomeDetails> getAllIncomesPages(Pageable pageable);

    @Query(value = "select * from income_details "
            +"where income_date between :startDate and :endDate",nativeQuery = true)
    Page<IncomeDetails> getAllIncomePagesWithDateRange(
            @Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
}
