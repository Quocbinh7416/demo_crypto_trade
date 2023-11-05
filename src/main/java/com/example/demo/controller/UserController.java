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

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse> buyCoin(@RequestBody TradeRequest request){
        ApiResponse apiResponse = userService.buyCoin(request);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse> sellCoin(@RequestBody TradeRequest request){
        ApiResponse apiResponse = userService.sellCoin(request);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/trade-market-info")
    public ResponseEntity<ApiResponse> getTradeMarketInfo(){
        ApiResponse apiResponse = userService.getTradeMarketInfo();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/trade-history/{id}")
    public ResponseEntity<ApiResponse> getUserTradeHistory(@PathVariable Long id){
        ApiResponse apiResponse = userService.getUserTradeHistory(id);
        return ResponseEntity.ok(apiResponse);
    }
}
