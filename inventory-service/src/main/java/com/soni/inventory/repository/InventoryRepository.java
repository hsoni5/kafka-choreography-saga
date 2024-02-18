package com.soni.inventory.repository;


import com.soni.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Iterable<Inventory> findByItem(String item);
}
