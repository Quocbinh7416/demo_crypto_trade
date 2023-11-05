package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.common.Constants.TIMESTAMP_PATTERN;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserBalanceDataRepository userBalanceDataRepository;
    @Autowired
    CryptoCoinRepository cryptoCoinRepository;
    @Autowired
    TradeMarketDataRepository tradeMarketDataRepository;
    @Autowired
    UserTradeHistoryRepository userTradeHistoryRepository;

    @Override
    public void createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setBalanceAmount(new BigDecimal(userDTO.getBalanceAmount()));
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user ->  UserDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .balanceAmount(user.getBalanceAmount().toPlainString())
                            .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findUserInfo(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        UserDTO userDTO = null;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<UserBalanceData> userBalanceDataList = userBalanceDataRepository.findByUserId(user.getId());
            Map<String, String> coinDataMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(userBalanceDataList)) {
                Map<Long, List<UserBalanceData>> mapByCoinId = userBalanceDataList.stream()
                        .collect(Collectors.groupingBy(UserBalanceData::getCoinId));
                List<CryptoCoin> coinList = cryptoCoinRepository.findByIdIn(mapByCoinId.keySet());
                Map<Long, String> mapByCoinName;
                if (!CollectionUtils.isEmpty(coinList)) {
                    mapByCoinName = coinList.stream().collect(Collectors.toMap(CryptoCoin::getId, CryptoCoin::getName));
                } else {
                    mapByCoinName = new HashMap<>();
                }
                mapByCoinId.forEach((k,v) -> {
                    String coinName = mapByCoinName.getOrDefault(k, "Unnamed");
                    BigDecimal amount = v.stream().map(UserBalanceData::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    coinDataMap.put(coinName, amount.toPlainString());
                });

            }
            userDTO = UserDTO.builder()
                    .name(user.getName())
                    .balanceAmount(user.getBalanceAmount().toPlainString())
                    .coinData(coinDataMap)
                    .build();
        }
        return userDTO;
    }

    @Override
    public ApiResponse buyCoin(TradeRequest request) {
        // use ask price for buy
        int compareAmount = request.getAskQuantity().compareTo(request.getAmount());
        BigDecimal expense = request.getAskPrice().multiply(request.getAmount());

        if (compareAmount < 0) {
            return new ApiResponse(HttpStatus.OK.name(), "INVALID AMOUNT", null);
        }
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isEmpty()) {
            return new ApiResponse(HttpStatus.OK.name(), "INVALID USER", null);
        }
        User user = userOptional.get();

        // valid balance amount
        BigDecimal balanceAmount = user.getBalanceAmount();
        if (balanceAmount.compareTo(expense) < 0) {
            return new ApiResponse(HttpStatus.OK.name(), "BALANCE AMOUNT IS NOT ENOUGH", null);
        }

        Optional<UserBalanceData> userBalanceDataOpt = userBalanceDataRepository
                .findByUserIdAndCoinId(user.getId(), request.getCryptoId());

        UserBalanceData userBalanceData = userBalanceDataOpt.orElse(null);

        // update UserBalanceData
        UserBalanceData savedUserBalanceData = saveUserBalanceData(request, userBalanceData, true);

        // update user
        balanceAmount = balanceAmount.subtract(expense);
        user.setBalanceAmount(balanceAmount);
        userRepository.save(user);

        // save history
        UserTradeHistory savedUserTradeHistory = saveUserTradeHistory(request, true);

        return new ApiResponse(HttpStatus.OK.name(), "BUY SUCCESSFULLY", null);
    }


    @Override
    public ApiResponse sellCoin(TradeRequest request) {
        // use bid price for buy
        int compareAmount = request.getBidQuantity().compareTo(request.getAmount());
        BigDecimal profit = request.getBidPrice().multiply(request.getAmount());

        if (compareAmount < 0) {
            return new ApiResponse(HttpStatus.OK.name(), "INVALID AMOUNT", null);
        }
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isEmpty()) {
            return new ApiResponse(HttpStatus.OK.name(), "INVALID USER", null);
        }
        User user = userOptional.get();

        // valid balance coin
        Optional<UserBalanceData> userBalanceDataOpt = userBalanceDataRepository
                .findByUserIdAndCoinId(user.getId(), request.getCryptoId());

        if (userBalanceDataOpt.isEmpty()) {
            return new ApiResponse(HttpStatus.OK.name(), "BALANCE COIN IS NOT ENOUGH", null);
        }

        // update UserBalanceData
        UserBalanceData savedUserBalanceData = saveUserBalanceData(request, userBalanceDataOpt.get(), false);

        // update user
        BigDecimal balanceAmount = user.getBalanceAmount();
        balanceAmount = balanceAmount.add(profit);
        user.setBalanceAmount(balanceAmount);
        userRepository.save(user);

        // save history
        UserTradeHistory savedUserTradeHistory = saveUserTradeHistory(request, false);

        return new ApiResponse(HttpStatus.OK.name(), "SELL SUCCESSFULLY", null);
    }

    private UserBalanceData saveUserBalanceData(TradeRequest request, UserBalanceData userBalanceData, boolean isBuy) {
        if (userBalanceData != null) {
            // update coin amount
            BigDecimal amount = isBuy ? userBalanceData.getAmount().add(request.getAmount()) :
                    userBalanceData.getAmount().subtract(request.getAmount());
            userBalanceData.setAmount(amount);

        } else {
            userBalanceData = new UserBalanceData();
            userBalanceData.setUserId(request.getUserId());
            userBalanceData.setCoinId(request.getCryptoId());
            userBalanceData.setAmount(request.getAmount());
        }
        return userBalanceDataRepository.save(userBalanceData);
    }

    private UserTradeHistory saveUserTradeHistory(TradeRequest request, boolean isBuy) {
        String tradeMarket;
        BigDecimal tradeMarketQuantity;
        BigDecimal tradeMarketPrice;
        if (isBuy) {
            tradeMarket = request.getAskTradeMarket();
            tradeMarketQuantity = request.getAskQuantity();
            tradeMarketPrice = request.getAskPrice();
        } else {
            tradeMarket = request.getBidTradeMarket();
            tradeMarketQuantity = request.getBidQuantity();
            tradeMarketPrice = request.getBidPrice();
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN).withZone(ZoneId.systemDefault());
        Instant tradeTimestamp = ZonedDateTime.parse(request.getTimestamp(), dateTimeFormatter).toInstant();
        UserTradeHistory userTradeHistory = new UserTradeHistory(request, isBuy, Instant.now(), tradeMarket,
                tradeMarketQuantity, tradeMarketPrice, tradeTimestamp);

        return userTradeHistoryRepository.save(userTradeHistory);
    }

    @Override
    public ApiResponse getTradeMarketInfo() {
        List<TradeMarketData> tradeMarketDataList = tradeMarketDataRepository.findAll();
        if(CollectionUtils.isEmpty(tradeMarketDataList)){
            return new ApiResponse(HttpStatus.OK.name(), "No data available", null);
        };
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN).withZone(ZoneId.systemDefault());
        Map<Long, TradeMarketData> mapByCoinId = tradeMarketDataList.stream()
                .collect(Collectors.toMap(TradeMarketData::getCoinId, Function.identity(), (o1,o2)->o1));
        Map<Long, CryptoCoin> coinMap = cryptoCoinRepository.findByIdIn(mapByCoinId.keySet())
                .stream().collect(Collectors.toMap(CryptoCoin::getId, Function.identity(), (o1,o2)-> o1));
        List<TradeMarketDTO> marketDTOList = new ArrayList<>();
        mapByCoinId.forEach((k,v)->{
            String cryptoName = "";
            String symbol = "";
            if (coinMap.containsKey(k)) {
                cryptoName = coinMap.get(k).getName();
                symbol = coinMap.get(k).getSymbol();
            }
            TradeMarketDTO tradeMarketDTO = TradeMarketDTO.builder()
                    .cryptoId(k)
                    .cryptoName(cryptoName)
                    .symbol(symbol)
                    .bidTradeMarket(v.getBidTradeMarket())
                    .bidPrice(v.getBidPrice().toPlainString())
                    .bidQuantity(v.getBidQuantity().toPlainString())
                    .askTradeMarket(v.getAskTradeMarket())
                    .askPrice(v.getAskPrice().toPlainString())
                    .askQuantity(v.getAskQuantity().toPlainString())
                    .timestamp(dateTimeFormatter.format(v.getTimestamp()))
                    .build();
            marketDTOList.add(tradeMarketDTO);
        });
        return ApiResponse.builder()
                .data(marketDTOList)
                .status("200")
                .build();
    }

    @Override
    public ApiResponse getUserTradeHistory(Long id) {
        List<UserTradeHistory> userTradeHistoryList =  userTradeHistoryRepository.findByUserId(id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            if (CollectionUtils.isEmpty(userTradeHistoryList)) {
                return new ApiResponse("200", "NO TRADE HISTORY", null);
            }
            Set<Long> coinIds = userTradeHistoryList.stream().map(UserTradeHistory::getCoinId).collect(Collectors.toSet());

            Map<Long, CryptoCoin> coinMapById = cryptoCoinRepository.findByIdIn(coinIds).stream()
                    .collect(Collectors.toMap(CryptoCoin::getId, Function.identity(), (o1,o2)->o1));

            List<UserTradeHistoryDTO> userTradeHistoryDTOS = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN).withZone(ZoneId.systemDefault());
            userTradeHistoryList.forEach(e->{
                String cryptoName = "";
                String symbol = "";
                if (coinMapById.containsKey(e.getCoinId())) {
                    cryptoName = coinMapById.get(e.getCoinId()).getName();
                    symbol = coinMapById.get(e.getCoinId()).getSymbol();
                }

                UserTradeHistoryDTO userTradeHistoryDTO = UserTradeHistoryDTO.builder()
                        .userName(userOptional.get().getName())
                        .cryptoName(cryptoName)
                        .symbol(symbol)
                        .timestamp(dateTimeFormatter.format(e.getTimestamp()))
                        .tradeOption(e.isBuy()? "BUY" : "SELL")
                        .tradeAmount(e.getAmount().toPlainString())
                        .tradeMarket(e.getTradeMarket())
                        .tradeMarketQuantity(e.getTradeMarketQuantity().toPlainString())
                        .tradeMarketPrice(e.getTradeMarketPrice().toPlainString())
                        .tradeMarketTimestamp(dateTimeFormatter.format(e.getTradeMarketTimestamp()))
                        .build();
                userTradeHistoryDTOS.add(userTradeHistoryDTO);
            });

            return new ApiResponse("200", "SUCCESSFULLY", userTradeHistoryDTOS);

        }
        return new ApiResponse("200", "INVALID USER ID", null);
    }
}
