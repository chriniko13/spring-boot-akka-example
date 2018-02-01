package com.chriniko.example.akkaspringexample.actor;

import akka.actor.AbstractLoggingActor;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.example.akkaspringexample.message.CrimeRecordsToProcessBatch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CrimeRecordProcessorSupervisor extends AbstractLoggingActor {

    //TODO add implementation...

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(CrimeRecordsToProcessBatch.class, msg -> {

                })
                .build();
    }
}
