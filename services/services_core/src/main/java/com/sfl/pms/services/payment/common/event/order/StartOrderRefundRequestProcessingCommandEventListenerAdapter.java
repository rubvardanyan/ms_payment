package com.sfl.pms.services.payment.common.event.order;

import com.sfl.pms.services.system.event.model.ApplicationEvent;
import com.sfl.pms.services.system.event.model.ApplicationEventListener;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 3:59 PM
 */
public abstract class StartOrderRefundRequestProcessingCommandEventListenerAdapter implements ApplicationEventListener {

    /* Constructors */
    public StartOrderRefundRequestProcessingCommandEventListenerAdapter() {
    }

    @Nonnull
    @Override
    public boolean subscribed(@Nonnull final ApplicationEvent applicationEvent) {
        return (applicationEvent instanceof StartOrderRefundRequestProcessingCommandEvent);
    }

    @Override
    public void process(@Nonnull final ApplicationEvent applicationEvent) {
        processOrderRefundRequestCreatedEvent((StartOrderRefundRequestProcessingCommandEvent) applicationEvent);
    }

    /* Abstract methods */
    protected abstract void processOrderRefundRequestCreatedEvent(final StartOrderRefundRequestProcessingCommandEvent event);
}
