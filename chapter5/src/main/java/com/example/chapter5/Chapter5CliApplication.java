package com.example.chapter5;

import com.pi4j.io.gpio.*;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter5CliApplication implements QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "LED", PinState.HIGH);
        final GpioPinDigitalInput sw = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, "Switch");
        pin.setShutdownOptions(true, PinState.LOW);

        for(int i = 0 ; i < 100000 ; i++) {
            if(sw.isHigh()) {
                pin.high();
            } else {
                pin.low();
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }

        gpio.shutdown();

        return 0;
    }
}
