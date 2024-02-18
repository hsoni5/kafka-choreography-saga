package com.soni.payment.repository;

import com.soni.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
     List<Payment> findByOrderId(long orderId);
}
