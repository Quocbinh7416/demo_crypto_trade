package com.example.demo.repository;

import com.example.demo.entity.TradeMarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeMarketDataRepository extends JpaRepository<TradeMarketData, Long> {
    Optional<TradeMarketData> findByCoinId(Long v);
}
