package com.endava.fraud.bank;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.loader.PactSource;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.endava.fraud.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@RunWith(PactRunner.class)
@Provider("fraud-service" )
@PactFolder("../the-bank/target/pacts")
public class TestAgainstTheBank {

    protected static ConfigurableApplicationContext application;

    /**
     * Run Interaction and perform response verification
     */
    @TestTarget
    public final Target target = new HttpTarget(8080);

    @BeforeClass
    public static void startSpring(){
        application = SpringApplication.run(Application.class);
    }

    @AfterClass
    public static void tearDown(){
        application.stop();
    }

    @State("should mark participant as fraud")
    public void should_mark_participant_as_fraud() {
        // setup provider (database etc...)
    }

    @State("should mark participant as not fraud")
    public void should_not_mark_participant_as_fraud() {
        //setup provider (database etc...)
    }
}