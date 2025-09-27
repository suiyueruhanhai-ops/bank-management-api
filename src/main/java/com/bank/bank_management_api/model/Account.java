// src/main/java/com/bank/bankmanagementapi/model/Account.java

package com.bank.bank_management_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data; 
import java.math.BigDecimal;

@Entity 
@Data   
// 实体类代表数据库中的一个表
public class Account {

    @Id // 主键
    private String cardNumber; // 用卡号作为唯一标识

    private String username;
    private String passwordHash; // 存储密码哈希值
    private String realName;
    private String phone;
    private BigDecimal balance; // 账户余额，使用 BigDecimal 保证金额精度
}