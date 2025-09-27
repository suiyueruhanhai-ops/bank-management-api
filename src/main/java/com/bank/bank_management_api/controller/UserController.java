// src/main/java/com/bank/bank_management_api/controller/UserController.java

package com.bank.bank_management_api.controller;

import com.bank.bank_management_api.model.Account;
import com.bank.bank_management_api.repository.AccountRepository;
import com.bank.bank_management_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user") // 用户相关的接口前缀
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private AccountRepository accountRepository; // 注入用于查询账户的 Repository
    
    @Autowired
    private UserService userService; // 注入业务逻辑服务

    // 辅助方法：从 Token 中提取卡号
    private String getCardNumberFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer TOKEN_")) {
            return authHeader.substring("Bearer TOKEN_".length()); 
        }
        return null; 
    }

    /**
     * GET /api/user/info: 查询个人用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String cardNumber = getCardNumberFromToken(authHeader);

        if (cardNumber == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未授权，请重新登录"));
        }

        Account account = accountRepository.findById(cardNumber).orElse(null);

        if (account == null) {
            return ResponseEntity.status(404).body(Map.of("code", 1, "message", "账户不存在"));
        }

        // 返回前端所需的全部用户信息
        Map<String, Object> userInfo = Map.of(
            "code", 0,
            "username", account.getUsername(),
            "realName", account.getRealName(),
            "phone", account.getPhone(),
            "cardNumber", account.getCardNumber(),
            "balance", account.getBalance() 
        );
        
        return ResponseEntity.ok(userInfo);
    }

    /**
     * PUT /api/user/info: 更新用户信息（姓名和电话）
     */
    @PutMapping("/info")
    public ResponseEntity<?> updateUserInfo(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, String> updateRequest
    ) {
        String cardNumber = getCardNumberFromToken(authHeader);
        if (cardNumber == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未授权，请重新登录"));
        }

        try {
            // 调用 Service 层处理更新逻辑
            Account updatedAccount = userService.updateUserInfo(
                cardNumber, 
                updateRequest.get("realName"), 
                updateRequest.get("phone")
            );

            // 返回更新后的完整数据给前端 Pinia Store
            Map<String, Object> response = Map.of(
                "code", 0,
                "message", "用户信息更新成功",
                "username", updatedAccount.getUsername(),
                "realName", updatedAccount.getRealName(),
                "phone", updatedAccount.getPhone(),
                "cardNumber", updatedAccount.getCardNumber(),
                "balance", updatedAccount.getBalance()
            );
            
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 1, "message", e.getMessage()));
        }
    }
}