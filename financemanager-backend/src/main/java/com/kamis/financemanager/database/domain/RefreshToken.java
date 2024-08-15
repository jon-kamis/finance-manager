package com.kamis.financemanager.database.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name="refresh_tokens")
public class RefreshToken {

    @Id
    @SequenceGenerator(name="refresh_tokens_id_seq", sequenceName="refresh_tokens_id_seq", allocationSize = 1)
    @GeneratedValue(generator="refresh_tokens_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @Column(name="token")
    private UUID token;

    @Column(name="username")
    private String username;

    @Column(name="expiration_dt")
    private Date expirationDate;
}
