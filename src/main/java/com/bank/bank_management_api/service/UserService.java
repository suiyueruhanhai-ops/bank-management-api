// src/main/java/com/bank/bank_management_api/service/UserService.java

package com.bank.bank_management_api.service;

import com.bank.bank_management_api.model.Account;
import com.bank.bank_management_api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private AccountRepository accountRepository;

    // --- 交易操作 (需要事务) ---

    @Transactional 
    public BigDecimal withdraw(String cardNumber, BigDecimal amount) {
        // ... (取款逻辑，从账户余额中减去 amount) ...
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { throw new RuntimeException("金额必须大于零"); }
        Account account = accountRepository.findById(cardNumber) .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (account.getBalance().compareTo(amount) < 0) { throw new RuntimeException("账户余额不足"); }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        return newBalance;
    }

    @Transactional 
    public BigDecimal deposit(String cardNumber, BigDecimal amount) {
        // ... (存款逻辑，向账户余额中加上 amount) ...
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { throw new RuntimeException("金额必须大于零"); }
        Account account = accountRepository.findById(cardNumber) .orElseThrow(() -> new RuntimeException("账户不存在"));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        return newBalance;
    }

    @Transactional 
    public BigDecimal transfer(String fromCard, String toCard, BigDecimal amount) {
        // ... (转账逻辑，从 fromCard 减去，向 toCard 加上) ...
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { throw new RuntimeException("金额必须大于零"); }
        if (fromCard.equals(toCard)) { throw new RuntimeException("不能向自己的账户转账"); }

        Account fromAccount = accountRepository.findById(fromCard) .orElseThrow(() -> new RuntimeException("转出卡号不存在"));
        Account toAccount = accountRepository.findById(toCard) .orElseThrow(() -> new RuntimeException("转入卡号不存在"));

        if (fromAccount.getBalance().compareTo(amount) < 0) { throw new RuntimeException("转出账户余额不足"); }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount); 
        return fromAccount.getBalance();
    }
    
    // --- 信息更新操作 (不需要事务) ---
    
    public Account updateUserInfo(String cardNumber, String newRealName, String newPhone) {
        // ... (用户信息更新逻辑) ...
        if (newRealName == null || newRealName.trim().isEmpty() || newPhone == null || newPhone.trim().isEmpty()) {
             throw new RuntimeException("姓名和电话不能为空");
        }
        
        Account account = accountRepository.findById(cardNumber)
            .orElseThrow(() -> new RuntimeException("账户不存在"));

        account.setRealName(newRealName);
        account.setPhone(newPhone);
        
        return accountRepository.save(account);
    }
}