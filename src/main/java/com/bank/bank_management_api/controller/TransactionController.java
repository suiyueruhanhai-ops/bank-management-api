// src/main/java/com/bank/bank_management_api/controller/TransactionController.java

package com.bank.bank_management_api.controller;

import com.bank.bank_management_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction") // 基础路径
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    @Autowired
    // 注入 UserService，用于处理所有交易业务逻辑
    private UserService transactionService; 

    // 辅助方法：从 Token 中提取卡号 (用于身份验证)
    private String getCardNumberFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer TOKEN_")) {
            return authHeader.substring("Bearer TOKEN_".length()); 
        }
        return null; 
    }

    /**
     * POST /api/transaction/withdraw: 取款接口
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, Object> requestBody
    ) {
        String cardNumber = getCardNumberFromToken(authHeader);
        
        if (cardNumber == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未授权，请重新登录"));
        }
        
        // 确保金额是数字类型
        BigDecimal amount;
        try {
            amount = new BigDecimal(requestBody.get("amount").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("code", 1, "message", "金额格式错误"));
        }

        try {
            // 调用 Service 层处理事务
            BigDecimal newBalance = transactionService.withdraw(cardNumber, amount);

            // 返回成功响应，包含新的账户余额
            return ResponseEntity.ok(Map.of(
                "code", 0,
                "message", "取款成功",
                "newBalance", newBalance 
            ));

        } catch (RuntimeException e) {
            // 处理 Service 层抛出的业务异常（如余额不足）
            return ResponseEntity.badRequest().body(Map.of("code", 1, "message", e.getMessage()));
        }
    }

    /**
     * POST /api/transaction/deposit: 存款接口
     */
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, Object> requestBody
    ) {
        String cardNumber = getCardNumberFromToken(authHeader);
        if (cardNumber == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未授权，请重新登录"));
        }
        
        BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());

        try {
            BigDecimal newBalance = transactionService.deposit(cardNumber, amount);

            return ResponseEntity.ok(Map.of(
                "code", 0,
                "message", "存款成功",
                "newBalance", newBalance 
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 1, "message", e.getMessage()));
        }
    }

    /**
     * POST /api/transaction/transfer: 转账接口
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, Object> requestBody
    ) {
        String fromCard = getCardNumberFromToken(authHeader);
        String toCard = (String) requestBody.get("toCardNumber");
        
        if (fromCard == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未授权，请重新登录"));
        }

        BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());

        try {
            BigDecimal newBalance = transactionService.transfer(fromCard, toCard, amount);
            return ResponseEntity.ok(Map.of(
                "code", 0,
                "message", "转账成功",
                "newBalance", newBalance 
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 1, "message", e.getMessage()));
        }
    }
}