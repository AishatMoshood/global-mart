package com.verraki.globalmart.stockmonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Inventory {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "warehouse_id")
        private Warehouse warehouse;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private Product product;

        private int stockLevel;

        private int reorderThreshold;

        private int reorderQuantity;

        private int leadTime;  // in days

        private double regionalDemandMultiplier; // Factor to scale reorder quantities based on region

}
