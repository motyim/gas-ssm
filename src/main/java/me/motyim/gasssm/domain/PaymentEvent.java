package me.motyim.gasssm.domain;

public enum PaymentEvent {
    PRE_AUTHORIZE,
    PRE_AUT_APPROVED,
    PRE_AUT_DECLINED,
    AUTHORIZE,
    AUTH_APPROVE,
    AUTH_DECLINE
}
