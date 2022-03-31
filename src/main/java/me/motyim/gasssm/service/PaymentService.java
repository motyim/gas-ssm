package me.motyim.gasssm.service;

import me.motyim.gasssm.domain.PaymentEvent;
import me.motyim.gasssm.domain.PaymentState;
import me.motyim.gasssm.modal.entites.Payment;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    String HEADER_NAME = "Payment_ID";

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(long paymentId);

    StateMachine<PaymentState, PaymentEvent> authPayment(long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuthPayment(long paymentId);

}
