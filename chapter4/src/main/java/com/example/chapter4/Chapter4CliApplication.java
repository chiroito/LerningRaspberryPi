package com.example.chapter4;


import com.pi4j.io.gpio.*;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter4CliApplication implements  QuarkusApplication {

    public static volatile boolean runningFlag = true;

    @Override
    public int run(String... args) throws Exception {

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_25, "LED", PinState.HIGH);
        pin.setShutdownOptions(true, PinState.LOW);

        while(runningFlag) {
            pin.high();
            TimeUnit.MILLISECONDS.sleep(500);
            pin.low();
            TimeUnit.MILLISECONDS.sleep(500);
        }

        gpio.shutdown();

        return 0;
    }
}
