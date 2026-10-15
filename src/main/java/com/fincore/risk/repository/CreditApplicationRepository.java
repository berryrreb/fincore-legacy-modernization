package com.fincore.risk.repository;

import com.fincore.risk.entity.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {

    List<CreditApplication> findAllByOrderByIdAsc();

    List<CreditApplication> findByStatusOrderByIdAsc(String status);
}
