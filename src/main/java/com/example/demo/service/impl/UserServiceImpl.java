package com.example.demo.service.impl;

import com.example.demo.entity.CryptoCoin;
import com.example.demo.entity.User;
import com.example.demo.entity.UserBalanceData;
import com.example.demo.model.ApiResponse;
import com.example.demo.model.TradeRequest;
import com.example.demo.model.UserDTO;
import com.example.demo.repository.CryptoCoinRepository;
import com.example.demo.repository.UserBalanceDataRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserBalanceDataRepository userBalanceDataRepository;
    @Autowired
    CryptoCoinRepository cryptoCoinRepository;

    @Override
    public void createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setBalanceAmount(userDTO.getBalanceAmount());
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user ->  UserDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .balanceAmount(user.getBalanceAmount())
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
                    .balanceAmount(user.getBalanceAmount())
                    .coinData(coinDataMap)
                    .build();
        }
        return userDTO;
    }

    @Override
    public ApiResponse tradeCoin(TradeRequest request) {
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        return null;
    }
}
