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

import java.net.InetSocketAddress;

public class SqsReactiveSocketBridge {
    private final String queueName;
    private final String region;
    private final InetSocketAddress destAddress;

    public SqsReactiveSocketBridge(final String queueName, final String region, final InetSocketAddress destAddress) {
        this.queueName = queueName;
        this.region = region;
        this.destAddress = destAddress;
    }

    public void start() {

    }
}
