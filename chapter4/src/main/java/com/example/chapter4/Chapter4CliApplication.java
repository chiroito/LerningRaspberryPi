package com.example.chapter4;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.platform.Platforms;
import io.quarkus.runtime.QuarkusApplication;

public class Chapter4CliApplication implements  QuarkusApplication {

    private static final int PIN_LED = 24; // PIN 18 = BCM 24

    @Override
    public int run(String... args) throws Exception {
        var pi4j = Pi4J.newAutoContext();
        Platforms platforms = pi4j.platforms();

        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
//                .id("led")
//                .name("LED Flasher")
                .address(PIN_LED)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
//                .provider("pigpio-digital-output");
;

        var led = pi4j.create(ledConfig);

        int pressCount = 0;
        while (pressCount < 5) {
            if (led.equals(DigitalState.HIGH)) {
                led.low();
            } else {
                led.high();
            }
            Thread.sleep(500 / (pressCount + 1));
        }

        pi4j.shutdown();

        return 0;
    }
}
