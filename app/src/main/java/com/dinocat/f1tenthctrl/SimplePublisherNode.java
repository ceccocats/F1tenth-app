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

package com.dinocat.f1tenthctrl;

import android.graphics.Bitmap;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.RosActivity;
import org.ros.internal.message.MessageInterfaceBuilder;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import race.drive_param;
import std_msgs.Bool;
import sensor_msgs.CompressedImage;

public class SimplePublisherNode extends AbstractNodeMain implements NodeMain {

    private static final String TAG = SimplePublisherNode.class.getSimpleName();
    private Publisher<drive_param> publisher;
    private Publisher<Bool> autoPub;
    private Subscriber<CompressedImage> imgSub;
    private MainActivity parent;

    public SimplePublisherNode(MainActivity parent) {
        super();
        this.parent = parent;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_drive");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(GraphName.of("drive_parameters"), drive_param._TYPE);
        autoPub = connectedNode.newPublisher(GraphName.of("eStop"), Bool._TYPE);

        imgSub = connectedNode.newSubscriber(
                GraphName.of("camera/left/image_rect_color/compressed"), CompressedImage._TYPE);
        final BitmapFromCompressedImage converter = new BitmapFromCompressedImage();

        imgSub.addMessageListener(new MessageListener<CompressedImage>() {
            @Override
            public void onNewMessage(CompressedImage compressedImage) {

            Bitmap bmp = converter.call(compressedImage);
            parent.updateImg(bmp);
            }
        });
    }

    public void sendData(int throttle, int steer) {

        MessageInterfaceBuilder msgb = new MessageInterfaceBuilder();

        // create and publish a simple string message
        drive_param msg = publisher.newMessage();
        msg.setVelocity(throttle);
        msg.setAngle(steer);
        publisher.publish(msg);
    }

    public void sendAutoMsg() {
        Bool b = autoPub.newMessage();
        b.setData(false);
        autoPub.publish(b);
    }
}
