package com.chriniko.example.akkaspringexample.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.Routee;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;
import com.chriniko.example.akkaspringexample.domain.CrimeRecord;
import com.chriniko.example.akkaspringexample.integration.akka.SpringAkkaExtension;
import com.chriniko.example.akkaspringexample.message.CrimeRecordsToProcess;
import com.chriniko.example.akkaspringexample.message.CrimeRecordsToProcessBatch;
import com.chriniko.example.akkaspringexample.util.ListPartitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CrimeRecordsProcessorSupervisor extends AbstractLoggingActor {

    @Autowired
    private SpringAkkaExtension springAkkaExtension;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private ListPartitioner listPartitioner;

    @Value("${crime.records.processor.supervisor.children}")
    private int childrenToCreate;

    private Router router;

    @Override
    public void preStart() throws Exception {

        log().info("Starting up...");

        List<Routee> routees = new ArrayList<>(childrenToCreate);

        for (int i = 0; i < childrenToCreate; i++) {

            //TODO add another dispatcher because we will have heavy I/O (db access)...
            ActorRef crimeRecordsProcessorChild =
                    actorSystem.actorOf(springAkkaExtension.props(SpringAkkaExtension.classNameToSpringName(CrimeRecordsProcessor.class)));

            getContext().watch(crimeRecordsProcessorChild);
            routees.add(new ActorRefRoutee(crimeRecordsProcessorChild));
        }

        router = new Router(new SmallestMailboxRoutingLogic(), routees);
    }

    @Override
    public void postStop() throws Exception {
        log().info("Shutting down...");
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(CrimeRecordsToProcessBatch.class, msg -> {

                    log().info("Routing work to children...");

                    final List<CrimeRecord> crimeRecords = msg.getCrimeRecords();

                    final List<List<CrimeRecord>> splittedCrimeRecordsForChildren = listPartitioner.partition(crimeRecords, childrenToCreate, false);

                    for (List<CrimeRecord> splittedCrimeRecordsForChild : splittedCrimeRecordsForChildren) {

                        router.route(new CrimeRecordsToProcess(splittedCrimeRecordsForChild), context().self());
                    }

                })
                .match(Terminated.class, msg -> {

                    ActorRef terminatedActor = msg.actor();

                    router = router.removeRoutee(terminatedActor);

                    ActorRef crimeRecordsProcessorChild =
                            actorSystem.actorOf(springAkkaExtension.props(SpringAkkaExtension.classNameToSpringName(CrimeRecordsProcessor.class)));

                    getContext().watch(crimeRecordsProcessorChild);

                    router.addRoutee(new ActorRefRoutee(crimeRecordsProcessorChild));

                })
                .build();
    }

}
