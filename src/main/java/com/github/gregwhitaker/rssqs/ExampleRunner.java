/*
 * Copyright 2016 Greg Whitaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gregwhitaker.rssqs;

import com.amazonaws.regions.Regions;

import java.net.InetSocketAddress;

/**
 * Runs the reactivesocket sqs example.
 */
public class ExampleRunner {

    /**
     * Main entry-point for this example.
     *
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        String queueName = System.getProperty("queueName");
        String region = System.getProperty("region");

        if (queueName == null) {
            throw new RuntimeException("The name of the queue is required to run this demo!");
        }

        if (region == null || region.isEmpty()) {
            throw new RuntimeException("The AWS region where the queue is located is required to run this demo!");
        } else {
            try {
                Regions.fromName(region);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(String.format("Region '%s' is not a valid AWS region identifier.", region), e);
            }
        }

        // Starts the SqsProducer which is responsible for generating test messages and placing
        // them on an Amazon SQS queue
        SqsProducer sqsProducer = new SqsProducer(queueName, region);
        sqsProducer.start();

        // Starts the ReactiveSocketProducer which is responsible for reading messages from the Amazon SQS queue
        // and sending them over a reactive socket channel to the consumer.
        SqsReactiveSocketBridge rsBridge = new SqsReactiveSocketBridge(queueName, region, new InetSocketAddress("127.0.0.1", 8080));
        rsBridge.start();

        // Starts the ReactiveSocketConsumer which is responsible for receiving messages from the ReactiveSocketProducer
        // and printing them to standard out.
        ReactiveSocketConsumer rsConsumer = new ReactiveSocketConsumer(new InetSocketAddress("127.0.0.1", 8080));
        rsConsumer.start();

        Thread.currentThread().join();
    }
}
