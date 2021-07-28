package com.example.chapter5;

import com.pi4j.io.gpio.*;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter503CliApplication implements QuarkusApplication {

    @Override
    public int run(String... args) {

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, PinState.LOW);
        final GpioPinDigitalInput sw = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        GpioPinListener myCallback = new GpioPinListenerDigital() {
            private PinState ledState = PinState.LOW;
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event){
                if(event.getState().isHigh()) {//GPIO RISING
                    ledState = PinState.LOW.equals(ledState) ? PinState.HIGH : PinState.LOW;
                    if (PinState.HIGH.equals(ledState)) {
                        pin.high();
                    } else {
                        pin.low();
                    }
                }
            }
        };
        sw.addListener(myCallback);

        try {
            while(true) {
                TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pin.setShutdownOptions(true, PinState.LOW);
        sw.setShutdownOptions(true, PinState.LOW, PinPullResistance.PULL_DOWN);
        gpio.shutdown();

        return 0;
    }
}
