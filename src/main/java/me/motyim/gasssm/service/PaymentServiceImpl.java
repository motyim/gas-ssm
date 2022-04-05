package me.motyim.gasssm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.motyim.gasssm.domain.PaymentEvent;
import me.motyim.gasssm.domain.PaymentState;
import me.motyim.gasssm.modal.entites.Payment;
import me.motyim.gasssm.modal.repo.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final StateMachineFactory<PaymentState,PaymentEvent> factory;
    private final PaymentStateChangeInterceptor interceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return repository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = buildContext(paymentId);
        sendEvent(paymentId,stateMachine,PaymentEvent.PRE_AUTHORIZE);
        return stateMachine;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authPayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = buildContext(paymentId);
        sendEvent(paymentId,stateMachine,PaymentEvent.AUTHORIZE);
        return stateMachine;
    }

    private void sendEvent(long paymentId,StateMachine<PaymentState, PaymentEvent> stateMachine,PaymentEvent event){
        log.info("send event :{}",event);
        Message<PaymentEvent> message = MessageBuilder.withPayload(event).setHeader(HEADER_NAME, paymentId).build();
        stateMachine.sendEvent(message);
        log.info("end send event :{}",event);
    }

    private StateMachine<PaymentState, PaymentEvent> buildContext(long paymentId) {
        Payment payment = repository.getById(paymentId);
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(String.valueOf(paymentId));

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                        .doWithAllRegions(sma ->{
                            sma.addStateMachineInterceptor(interceptor);
                            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
                        });

        stateMachine.start();
        return stateMachine;
    }


}
