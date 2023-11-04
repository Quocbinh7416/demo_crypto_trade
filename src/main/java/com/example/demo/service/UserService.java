package com.example.demo.service;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.TradeRequest;
import com.example.demo.model.UserDTO;

import java.util.List;

public interface UserService {
    void createUser(UserDTO userDTO);

    List<UserDTO> findAll();

    UserDTO findUserInfo(Long id);

    ApiResponse tradeCoin(TradeRequest request);
}
