package org.testcontainers.bookstore.common;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeChecker implements AfterAllCallback, BeforeAllCallback, BeforeTestExecutionCallback {

    public static Map<String, Instant> started = new ConcurrentHashMap<>();
    public static Map<String, Instant> firstTestStarting = new ConcurrentHashMap<>();
    public static Map<String, Instant> stopped = new ConcurrentHashMap<>();
    public static Map<String, String> sessions = new ConcurrentHashMap<>();

    @Override
    public void beforeAll(ExtensionContext ec) throws Exception {
        started.put(className(ec), Instant.now());
    }

    @Override
    public void afterAll(ExtensionContext ec) throws Exception {
        String className = className(ec);
        System.out.println("PREPARING REPORT FOR " + className);
        try {
            stopped.put(className, Instant.now());
            sessions.put(className, DockerClientFactory.SESSION_ID);

            Duration total = Duration.between(started.get(className), stopped.get(className));
            Duration init = Duration.between(started.get(className), firstTestStarting.get(className));

            System.out.printf("REPORT;%-90s;%7d;%7d; %s;%n%n", className, total.toMillis(), init.toMillis(), sessions.get(className));

        } catch (Exception e) {
            System.out.println("Cannot create report for " + className);
            e.printStackTrace(System.out);
        }
    }


    @Override
    public void beforeTestExecution(ExtensionContext ec) throws Exception {
        String className = className(ec);
        firstTestStarting.putIfAbsent(className, Instant.now());
    }

    private String className(ExtensionContext ec) {
        return ec.getTestClass().get().getCanonicalName();
    }
}