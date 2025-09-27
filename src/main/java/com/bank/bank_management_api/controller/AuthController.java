// src/main/java/com/bank/bankmanagementapi/controller/AuthController.java

package com.bank.bank_management_api.controller;

import com.bank.bank_management_api.model.Account;
import com.bank.bank_management_api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证控制器：处理用户登录和未来可能的注册/退出功能。
 */
@RestController
@RequestMapping("/api/auth") // 所有接口路径前缀为 /api/auth
// 允许来自 Vue 前端开发服务器（http://localhost:5173）的跨域请求
@CrossOrigin(origins = "http://localhost:5173") 
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * 用户登录接口
     * URL: POST /api/auth/login
     * 请求体: { "username": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 1. 通过用户名查询账户信息
        Optional<Account> accountOpt = accountRepository.findByUsername(username);

        // 验证用户名是否存在
        if (accountOpt.isEmpty()) {
            // 返回 401 Unauthorized，并给出统一的错误提示
            return ResponseEntity.status(401).body(Map.of("code", 1, "message", "用户名或密码错误"));
        }

        Account account = accountOpt.get();

        // 2. 验证密码
        // 🚨 警告：此处仅为实训验证简化，实际应使用 BCrypt 等加密比对！
        if (!account.getPasswordHash().equals(password)) { 
            return ResponseEntity.status(401).body(Map.of("code", 1, "message", "用户名或密码错误"));
        }

        // 3. 登录成功，准备响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0); // 约定 code=0 表示成功
        response.put("message", "登录成功");
        
        // 🔑 关键：返回一个 Token，前端 Pinia 将会保存它
        String token = "TOKEN_" + account.getCardNumber(); // 简单的伪造 Token
        response.put("token", token); 
        
        // **状态管理数据：** 返回前端需要的用户基本信息和余额
        response.put("userData", Map.of(
            "username", account.getUsername(),
            "cardNumber", account.getCardNumber(),
            "balance", account.getBalance()
        ));
        
        return ResponseEntity.ok(response);
    }
}