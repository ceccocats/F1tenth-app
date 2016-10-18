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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dinocat.f1tenthctrl.joystickcontroller.JoyStickClass;

import org.apache.xmlrpc.server.XmlRpcErrorLogger;
import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    RelativeLayout layout_joystick0, layout_joystick1;
    TextView textView1, textView2, textView4, textView5;
    SeekBar limitY, limitX;
    Button auto;

    public static JoyStickClass js0, js1;
    public static ImageView img;
    public SimplePublisherNode node;



    public MainActivity() {
        super("F1tenth controller", "F1tenth controller");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);

        img = (ImageView) findViewById(R.id.imageView);

        layout_joystick0 = (RelativeLayout)findViewById(R.id.layout_joystick0);
        layout_joystick1 = (RelativeLayout)findViewById(R.id.layout_joystick1);

        View.OnTouchListener seekList = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                textView4.setText("Limit: " + limitX.getProgress());
                textView5.setText("Limit: " + limitY.getProgress());
                return false;
            }
        };

        limitX = (SeekBar) findViewById(R.id.limitX);
        limitX.setOnTouchListener(seekList);
        limitX.setProgress(100);

        limitY = (SeekBar) findViewById(R.id.limitY);
        limitY.setOnTouchListener(seekList);
        limitY.setProgress(100);

        auto = (Button) findViewById(R.id.buttonAuto);
        auto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                node.sendAutoMsg();
                return false;
            }
        });

        js0 = new JoyStickClass(getApplicationContext(), layout_joystick0, R.drawable.image_button);
        js0.setStickSize(150, 150);
        js0.setLayoutSize(500, 500);
        js0.setLayoutAlpha(150);
        js0.setStickAlpha(100);
        js0.setPossibleMove(false, true);
        js0.update(null);


        js1 = new JoyStickClass(getApplicationContext(), layout_joystick1, R.drawable.image_button);
        js1.setStickSize(150, 150);
        js1.setLayoutSize(500, 500);
        js1.setLayoutAlpha(150);
        js1.setStickAlpha(100);
        js1.setPossibleMove(true, false);
        js1.update(null);

        layout_joystick0.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js0.update(arg1);

                sendData();
                return true;
            }
        });
        layout_joystick1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js1.update(arg1);

                sendData();
                return true;
            }
        });


    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        node = new SimplePublisherNode(this);

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(node, nodeConfiguration);
    }

    public void sendData() {
        int Xlim = limitX.getProgress();
        int Ylim = limitY.getProgress();

        int throttle = js0.getYvalue(-Xlim);
        int steer = js1.getXvalue(Ylim);
        node.sendData(throttle, steer);

        textView2.setText("STEER: " + steer);
        textView1.setText("THROTTLE: " + throttle);
    }

    public void updateImg(final Bitmap bmp) {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                img.setImageBitmap(bmp);
            }
        }));
    }
}
