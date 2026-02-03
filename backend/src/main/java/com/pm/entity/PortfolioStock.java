package com.pm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "portfolio_stock",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"stock_id"})
        }
)
public class PortfolioStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private LocalDateTime createdAt = LocalDateTime.now();

    public PortfolioStock() {}

    public PortfolioStock(Stock stock) {
        this.stock = stock;
    }

}
