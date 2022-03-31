package me.motyim.gasssm.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final StateMachineFactory<PaymentState,PaymentEvent> factory;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return repository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = buildContext(paymentId);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authPayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = buildContext(paymentId);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuthPayment(long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = buildContext(paymentId);
        return null;
    }

    private void sendEvent(long paymentId,StateMachine<PaymentState, PaymentEvent> stateMachine,PaymentEvent event){
        Message<PaymentEvent> message = MessageBuilder.withPayload(event).setHeader(HEADER_NAME, paymentId).build();
        stateMachine.sendEvent(message);
    }

    private StateMachine<PaymentState, PaymentEvent> buildContext(long paymentId) {
        Payment payment = repository.getById(paymentId);
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(String.valueOf(paymentId));

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                        .doWithAllRegions(sma ->{
                            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
                        });

        stateMachine.start();
    }


}
