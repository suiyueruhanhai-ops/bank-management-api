// src/main/java/com/bank/bankmanagementapi/repository/AccountRepository.java

package com.bank.bank_management_api.repository;

import com.bank.bank_management_api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository 提供了基本的 CRUD 操作
// 第一个参数是实体类名，第二个是主键的类型
public interface AccountRepository extends JpaRepository<Account, String> {

    // Spring Data JPA 会根据方法名自动生成 SQL 查询语句：
    // SELECT * FROM account WHERE username = ?
    Optional<Account> findByUsername(String username);
}