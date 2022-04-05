package me.motyim.gasssm.config;

import lombok.extern.slf4j.Slf4j;
import me.motyim.gasssm.domain.PaymentEvent;
import me.motyim.gasssm.domain.PaymentState;
import me.motyim.gasssm.service.PaymentService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

import static me.motyim.gasssm.domain.PaymentState.*;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {


    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(AUTH)
                .end(PRE_AUTH_ERROR)
                .end(AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(NEW).target(NEW).event(PaymentEvent.PRE_AUTHORIZE)
                .action(preAuthAction())
                .and()
                .withExternal().source(NEW).target(PRE_AUTH).event(PaymentEvent.PRE_AUT_APPROVED)
                .and()
                .withExternal().source(NEW).target(PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUT_DECLINED)
                .and()
                .withExternal().source(PRE_AUTH).target(PRE_AUTH).event(PaymentEvent.AUTHORIZE)
                .action(authAction())
                .and()
                .withExternal().source(PRE_AUTH).target(AUTH).event(PaymentEvent.AUTH_APPROVE)
                .and()
                .withExternal().source(PRE_AUTH).target(AUTH_ERROR).event(PaymentEvent.AUTH_DECLINE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        config.withConfiguration().listener(new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("State change from {} ,to {}",from,to);
            }
        });
    }

    private Action<PaymentState,PaymentEvent> preAuthAction(){
        return stateContext -> {
          log.info("# preAuthAction");
          if(new Random().nextInt(10) < 8){
              log.info("approved");
              stateContext.getStateMachine().sendEvent(
                      MessageBuilder
                              .withPayload(PaymentEvent.PRE_AUT_APPROVED)
                              .setHeader(PaymentService.HEADER_NAME,stateContext.getMessageHeader(PaymentService.HEADER_NAME))
                              .build()
              );
          }else{
              log.info("Declined");
              stateContext.getStateMachine().sendEvent(
                      MessageBuilder
                              .withPayload(PaymentEvent.PRE_AUT_DECLINED)
                              .setHeader(PaymentService.HEADER_NAME,stateContext.getMessageHeader(PaymentService.HEADER_NAME))
                              .build()
              );
          }
        };
    }

    private Action<PaymentState, PaymentEvent> authAction() {
        return stateContext -> {
            log.info("# AuthAction");
            if(new Random().nextInt(10) < 8){
                log.info("approved");
                stateContext.getStateMachine().sendEvent(
                        MessageBuilder
                                .withPayload(PaymentEvent.AUTH_APPROVE)
                                .setHeader(PaymentService.HEADER_NAME,stateContext.getMessageHeader(PaymentService.HEADER_NAME))
                                .build()
                );
            }else{
                log.info("Declined");
                stateContext.getStateMachine().sendEvent(
                        MessageBuilder
                                .withPayload(PaymentEvent.AUTH_DECLINE)
                                .setHeader(PaymentService.HEADER_NAME,stateContext.getMessageHeader(PaymentService.HEADER_NAME))
                                .build()
                );
            }
        };
    }
}
