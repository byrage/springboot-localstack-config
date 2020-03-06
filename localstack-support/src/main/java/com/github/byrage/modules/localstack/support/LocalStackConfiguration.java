package com.github.byrage.modules.localstack.support;

import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile("local")
@Slf4j
@Order
@Configuration
public class LocalStackConfiguration {

    public static final String BEAN_NAME = "localStackConfiguration";
    private static final List<LocalStackContainer.Service> SUPPORTED_SERVICE = Arrays.asList(LocalStackContainer.Service.SQS, LocalStackContainer.Service.DYNAMODB);
    private static final String SECRET_KEY = "secretkey";
    private static final String ACCESS_KEY = "accesskey";
    private static LocalStackContainer container;
    private static boolean isDockerRunning = false;
    private boolean isContainerRunning = false;

    @PostConstruct
    private void start() {

        if (isDockerLocalStackRunning()) {
            log.info("Docker localstack is already running. skip");
            isDockerRunning = true;
            return;
        }

        try {
            log.info("Ready for Running localstack containers");
            container = new LocalStackContainer()
                    .withServices(SUPPORTED_SERVICE.toArray(new LocalStackContainer.Service[0]));
            container.setPortBindings(bindServicePort());
            container.start();
            isContainerRunning = true;
            log.info("Complete Running localstack containers");
        } catch (Exception e) {
            log.info("Skip Running localstack containers");
            isContainerRunning = false;
        }
    }

    @PreDestroy
    private void stop() {
        if (!isContainerRunning) {
            log.info("Skip stopping localstack containers");
            return;
        }
        log.info("Stop localstack containers");
        container.stop();
    }

    public boolean isDockerLocalStackRunning() {
        return SUPPORTED_SERVICE.stream()
                .map(LocalStackContainer.Service::getPort)
                .allMatch(this::checkPort);
    }

    public boolean checkPort(int port) {
        try (Socket socket = new Socket("localhost", port)) {
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public boolean isInitFinished() {
        return isContainerRunning || isDockerRunning;
    }

    private List<String> bindServicePort() {
        return SUPPORTED_SERVICE.stream().map(LocalStackContainer.Service::getPort).map(port -> port + ":" + port).collect(Collectors.toList());
    }

    public static AwsClientBuilder.EndpointConfiguration getEndpointConfiguration(LocalStackContainer.Service service) {
        if (isNotAvailable()) {
            throw new IllegalStateException("localstack containers is not running");
        }
        return new AwsClientBuilder.EndpointConfiguration("http://localhost:"+service.getPort(), Regions.AP_NORTHEAST_2.getName());
    }

    public static AWSCredentialsProvider getCredentials() {
        if (isNotAvailable()) {
            throw new IllegalStateException("localstack containers is not running");
        }
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
    }

    public static AwsCredentialsProvider getCredentialsProvider() {
        if (isNotAvailable()) {
            throw new IllegalStateException("localstack containers is not running");
        }

        return StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY));
    }

    private static boolean isNotAvailable() {
        return container == null && !isDockerRunning;
    }
}
