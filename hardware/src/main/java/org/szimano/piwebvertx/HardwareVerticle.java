package org.szimano.piwebvertx;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.vertx.java.platform.Verticle;

import java.util.Date;

public class HardwareVerticle extends Verticle {

    public void start() {

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        System.out.println("GPIO LOADED");

        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println(new Date() + "Button change");

                vertx.eventBus().publish("buttonbus", String.valueOf(event.getState().getValue()));
            }
        });
    }
}
