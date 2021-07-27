package com.example.chapter4;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter4CliApplication implements  QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {

        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22);

        for(int i = 0 ; i < 5 ; i++) {
            if(led.isLow()) {
                led.high();
            }if(led.isHigh()){
                led.low();
            }

            TimeUnit.SECONDS.sleep(1);
        }
        return 0;
    }
}
