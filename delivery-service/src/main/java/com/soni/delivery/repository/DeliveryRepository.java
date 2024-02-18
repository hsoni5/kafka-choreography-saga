package com.soni.delivery.repository;

import com.soni.delivery.entity.Delivery;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
