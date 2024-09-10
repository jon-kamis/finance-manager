package com.kamis.financemanager.database.domain;

import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @SequenceGenerator(name = "incomes_id_seq", sequenceName = "incomes_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "incomes_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "account_name")
    private String name;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "frequency")
    private PaymentFrequencyEnum frequency;

    @Column(name = "category")
    private TransactionCategoryEnum category;

    @Embedded
    private AuditInfo auditInfo;
}
