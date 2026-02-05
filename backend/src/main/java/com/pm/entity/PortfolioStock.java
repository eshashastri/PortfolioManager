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
    private int quantity;
    private double avgBuyPrice;
    private String sector;
    public PortfolioStock() {}

    public int getQuantity() {
        return quantity;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public void setAvgBuyPrice(double avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public PortfolioStock(Stock stock) {
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
