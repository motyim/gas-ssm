package me.motyim.gasssm.modal.repo;

import me.motyim.gasssm.modal.entites.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
