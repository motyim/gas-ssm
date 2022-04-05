package me.motyim.gasssm.service;

import lombok.extern.slf4j.Slf4j;
import me.motyim.gasssm.domain.PaymentEvent;
import me.motyim.gasssm.domain.PaymentState;
import me.motyim.gasssm.modal.entites.Payment;
import me.motyim.gasssm.modal.repo.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Slf4j
@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository repository;

    Payment payment;

    @BeforeEach
    void setup(){
        payment = Payment.builder().amount(BigDecimal.valueOf(12.90)).build();
    }

    @Test
    @Transactional
    void preAuth() {
        log.info("Test : new payment");
        Payment savedPayment = paymentService.newPayment(this.payment);

        log.info("Test : pre auth");
        paymentService.preAuth(savedPayment.getId());


        Payment preAuthPayment = repository.getById(savedPayment.getId());
        log.info("{}",preAuthPayment);
    }

    @Test
    @RepeatedTest(10)
    @Transactional
    void testAuth() {
        log.info("Test : new payment");
        Payment savedPayment = paymentService.newPayment(this.payment);

        log.info("Test : pre auth");
        StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.preAuth(savedPayment.getId());

        if(stateMachine.getState().getId() == PaymentState.PRE_AUTH){
            log.info("payment is preAuthed");
            StateMachine<PaymentState, PaymentEvent> authState = paymentService.authPayment(savedPayment.getId());
            log.info("the state is {}",authState.getState().getId());
        }else{
            log.info("payment is preAuthed declined");
        }
    }
}