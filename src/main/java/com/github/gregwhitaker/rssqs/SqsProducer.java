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
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.github.gregwhitaker.rxsqs.ReactiveSqsClient;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Observable;
import rx.RxReactiveStreams;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SqsProducer {
    private final String queueName;
    private final ReactiveSqsClient rxSqsClient;

    public SqsProducer(final String queueName, final String region) {
        this.queueName = queueName;
        this.rxSqsClient = new ReactiveSqsClient(Regions.fromName(region));
    }

    public void start() {
        rxSqsClient.getQueueUrlAsync(queueName)
                .last()
                .map(GetQueueUrlResult::getQueueUrl)
                .subscribe(url -> {
                    CountDownLatch latch = new CountDownLatch(Integer.MAX_VALUE);

                    // Observable that emits a message every second
                    RxReactiveStreams
                            .toPublisher(Observable
                                    .interval(1_000, TimeUnit.MILLISECONDS)
                                    .map(i -> String.format("This is message %s", i)))
                            .subscribe(new Subscriber<String>() {
                                @Override
                                public void onSubscribe(Subscription s) {
                                    s.request(Long.MAX_VALUE);
                                }

                                @Override
                                public void onNext(String s) {
                                    // Take the message and put it on the SQS queue
                                    rxSqsClient.sendMessageAsync(url, s)
                                            .subscribe(result -> {
                                                latch.countDown();
                                            });
                                }

                                @Override
                                public void onError(Throwable t) {
                                    t.printStackTrace();
                                    latch.countDown();
                                }

                                @Override
                                public void onComplete() {
                                    latch.countDown();
                                }
                            });

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }
}
