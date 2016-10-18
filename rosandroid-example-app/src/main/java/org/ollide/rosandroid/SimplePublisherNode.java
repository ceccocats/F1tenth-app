/*
 * Copyright (C) 2014 Oliver Degener.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ollide.rosandroid;

import org.ros.internal.message.MessageInterfaceBuilder;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import race.drive_param;
import std_msgs.Bool;

public class SimplePublisherNode extends AbstractNodeMain implements NodeMain {

    private static final String TAG = SimplePublisherNode.class.getSimpleName();
    Publisher<drive_param> publisher;
    Publisher<Bool> autoPub;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_drive");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("drive_parameters"), drive_param._TYPE);
        autoPub = connectedNode.newPublisher(GraphName.of("eStop"), Bool._TYPE);
    }

    public void sendData(int throttle, int steer) {

        MessageInterfaceBuilder msgb = new MessageInterfaceBuilder();

        // create and publish a simple string message
        drive_param msg = publisher.newMessage();
        msg.setVelocity(throttle);
        msg.setAngle(-steer);
        publisher.publish(msg);
    }

    public void sendAutoMsg() {
        Bool b = autoPub.newMessage();
        b.setData(false);
        autoPub.publish(b);
    }
}
