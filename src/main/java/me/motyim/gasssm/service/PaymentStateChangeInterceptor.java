package me.motyim.gasssm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.motyim.gasssm.domain.PaymentEvent;
import me.motyim.gasssm.domain.PaymentState;
import me.motyim.gasssm.modal.entites.Payment;
import me.motyim.gasssm.modal.repo.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository repository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine, StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        log.info("# interceptor");
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((Long) msg.getHeaders().getOrDefault(PaymentService.HEADER_NAME, -1L)))
                .ifPresent(paymentId -> {
                    Payment payment = repository.getById(paymentId);
                    payment.setState(state.getId());
                    repository.save(payment);
                });
    }
}
