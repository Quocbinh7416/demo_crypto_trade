package com.example.demo.repository;

import com.example.demo.entity.UserBalanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBalanceDataRepository extends JpaRepository<UserBalanceData, Long> {
    List<UserBalanceData> findByUserId(Long id);
}
