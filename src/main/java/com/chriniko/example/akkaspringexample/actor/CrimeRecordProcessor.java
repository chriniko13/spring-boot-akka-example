package com.chriniko.example.akkaspringexample.actor;

import akka.actor.AbstractLoggingActor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CrimeRecordProcessor extends AbstractLoggingActor {

    @Override
    public Receive createReceive() {
        return null;
    }
}
