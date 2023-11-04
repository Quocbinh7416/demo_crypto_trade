package com.example.demo.repository;

import com.example.demo.entity.CryptoCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CryptoCoinRepository extends JpaRepository<CryptoCoin, Long> {
    List<CryptoCoin> findByIdIn(Set<Long> coinIdSet);
}
