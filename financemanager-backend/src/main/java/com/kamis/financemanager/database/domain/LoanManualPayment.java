package com.kamis.financemanager.database.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="loan_manual_payments")
public class LoanManualPayment {
    @Id
    @SequenceGenerator(name="loan_manual_payments_id_seq", sequenceName="loan_manual_payments_id_seq", allocationSize = 1)
    @GeneratedValue(generator="loan_manual_payments_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(name = "amount")
    private Float amount;

    @Column(name="effective_dt")
    private Date effectiveDate;

    @Column(name="expiration_dt")
    private Date expirationDate;

    @Embedded
    private AuditInfo auditInfo;
}
