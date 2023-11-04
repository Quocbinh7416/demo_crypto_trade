package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.TradeRequest;
import com.example.demo.model.UserDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("all")
    public List<UserDTO> findAllUsers(){
        List<UserDTO> userDTOList = userService.findAll();
        return userDTOList;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserInfo(@PathVariable Long id){
        UserDTO userDTO = userService.findUserInfo(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> tradeCoin(@RequestBody TradeRequest request){
        ApiResponse apiResponse = userService.tradeCoin(request);
        return ResponseEntity.ok(apiResponse);
    }
}
