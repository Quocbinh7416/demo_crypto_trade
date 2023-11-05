package com.example.demo.service.impl;

import com.example.demo.entity.CryptoCoin;
import com.example.demo.entity.TradeMarketData;
import com.example.demo.model.market.*;
import com.example.demo.repository.CryptoCoinRepository;
import com.example.demo.repository.TradeMarketDataRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.demo.common.Constants.*;

@Service
@Slf4j
public class ScheduleServiceImpl {
    @Autowired
    CryptoCoinRepository cryptoCoinRepository;
    @Autowired
    TradeMarketDataRepository tradeMarketDataRepository;
    @Scheduled(fixedDelay = 10000)
    public void scheduleFixedDelayTask() {
        getTradeMarketData();
    }

    private void getTradeMarketData() {
        log.info("TIME START getTradeMarketData -- " + System.currentTimeMillis());
        final Instant instant = Instant.now();
        TradeMarketResponse tradeMarketResponse = new TradeMarketResponse();
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        CompletableFuture<Void> houbiFuture = CompletableFuture.runAsync(() -> this.getHoubiData(tradeMarketResponse));
        CompletableFuture<Void> binanceFuture = CompletableFuture.runAsync(() -> this.getBinanceData(tradeMarketResponse));
        tasks.add(houbiFuture);
        tasks.add(binanceFuture);
        tasks.forEach(CompletableFuture::join);
        updateTradeData(tradeMarketResponse, instant);
        log.info("TIME END getTradeMarketData" + System.currentTimeMillis());
    }

    private void getHoubiData(TradeMarketResponse tradeMarketResponse) {
        log.info("getHoubiData TIME START -- " + System.currentTimeMillis());
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> houbiApiResponse = restTemplate.getForEntity(HOUBI_URL, String.class);
        final Gson gson = new Gson();
        final HoubiResponse houbiResponse = gson.fromJson(houbiApiResponse.getBody(), HoubiResponse.class);
        tradeMarketResponse.setHuobiData(houbiResponse.getData());
        log.info("getHoubiData TIME END -- " + System.currentTimeMillis());
    }

    private void getBinanceData(TradeMarketResponse tradeMarketResponse) {
        log.info("getBinanceData TIME START -- " + System.currentTimeMillis());
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> binanceApiResponse = restTemplate.getForEntity(BINANCE_URL, String.class);
        final Gson gson = new Gson();
        final List<BinanceData> binanceDataList = gson.fromJson(binanceApiResponse.getBody(),
                new TypeToken<List<BinanceData>>() {}.getType());
        tradeMarketResponse.setBinanceData(binanceDataList);
        log.info("getBinanceData TIME END -- " + System.currentTimeMillis());
    }

    private void updateTradeData(TradeMarketResponse tradeMarketResponse, Instant instant) {
        List<CryptoCoin> coinList = cryptoCoinRepository.findAll();
        Map<String, Long> coinBySymbol = coinList.stream().collect(Collectors.toMap(CryptoCoin::getSymbol, CryptoCoin::getId));
        Map<String, List<MarketInfo>> marketMap;
        if (!CollectionUtils.isEmpty(tradeMarketResponse.getBinanceData())) {
            marketMap = tradeMarketResponse.getBinanceData().stream()
                    .collect(Collectors.toMap(BinanceData::getSymbol, e-> {
                        MarketInfo marketInfo = MarketInfo.builder()
                                .marketName(BINANCE)
                                .bidPrice(e.getBidPrice())
                                .bidQty(e.getBidQty())
                                .askPrice(e.getAskPrice())
                                .askQty(e.getAskQty())
                                .build();
                        return new ArrayList<>(Collections.singletonList(marketInfo));
                    }));
        } else {
            marketMap = new HashMap<>();
        }

        if (!CollectionUtils.isEmpty(tradeMarketResponse.getHuobiData())) {
            tradeMarketResponse.getHuobiData().forEach(e -> {
                String symbol = e.getSymbol().toUpperCase();
                MarketInfo marketInfo = MarketInfo.builder()
                        .marketName(HOUBI)
                        .bidPrice(e.getBid())
                        .bidQty(e.getBidSize())
                        .askPrice(e.getAsk())
                        .askQty(e.getAskSize())
                        .build();
                if (marketMap.containsKey(symbol)) {
                    marketMap.get(symbol).add(marketInfo);
                } else {
                    marketMap.put(symbol, new ArrayList<>(Collections.singletonList(marketInfo)));
                }
            });
        }

        coinBySymbol.forEach((k,v)->{
            if (marketMap.containsKey(k)) {
                List<MarketInfo> marketInfoList = marketMap.get(k);
                TradeMarketData temp = new TradeMarketData(v, marketInfoList);
                temp.setTimestamp(instant);
                Optional<TradeMarketData> tradeMarketDataOpt = tradeMarketDataRepository.findByCoinId(v);
                tradeMarketDataOpt.ifPresent(tradeMarketData -> temp.setId(tradeMarketData.getId()));
                tradeMarketDataRepository.save(temp);
            }
        });
    }
}
