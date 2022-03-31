package me.motyim.gasssm.config;

import lombok.extern.slf4j.Slf4j;
import me.motyim.gasssm.domain.PaymentEvent;
import me.motyim.gasssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine(){

        assertNotNull(factory);

        StateMachine<PaymentState, PaymentEvent> state = factory.getStateMachine();
        state.start();

        assertNotNull(state);
        log.info("Current State : {} ",state.getState().toString());


        state.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        log.info("Current State : {} ",state.getState().toString());

        state.sendEvent(PaymentEvent.PRE_AUT_APPROVED);
        log.info("Current State : {} ",state.getState().toString());

        state.sendEvent(PaymentEvent.PRE_AUT_DECLINED);
        log.info("Current State : {} ",state.getState().toString());

    }
}