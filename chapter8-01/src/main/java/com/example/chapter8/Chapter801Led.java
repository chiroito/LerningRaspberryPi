package com.example.chapter8;

import com.pi4j.io.gpio.*;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter801Led implements QuarkusApplication {

    private int readadc(int adcnum, GpioPinDigitalOutput clockpin, GpioPinDigitalOutput mosipin, GpioPinDigitalInput misopin, GpioPinDigitalOutput cspin) {
        if (adcnum > 7 || adcnum < 0) return -1;
        cspin.high();
        clockpin.low();
        cspin.low();

        int commandout = adcnum | 0x18;
        commandout <<= 3;
        for (int i = 0; i < 5; i++) {
            if ((commandout & 0x80) > 0) {
                mosipin.high();
            } else {
                mosipin.low();
            }
            commandout <<= 1;
            clockpin.high();
            clockpin.low();
        }
        int adcout = 0;
        for (int i = 0; i < 13; i++) {
            clockpin.high();
            clockpin.low();
            adcout <<= 1;
            if (i > 0 && misopin.isHigh()) {
                adcout |= 0x1;
            }
        }
        cspin.high();
        return adcout;
    }

    @Override
    public int run(String... args) {

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput spics = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10);
        final GpioPinDigitalOutput spiclk = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14);
        final GpioPinDigitalOutput spimosi = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12);
        final GpioPinDigitalInput spimiso = gpio.provisionDigitalInputPin(RaspiPin.GPIO_13);
        spics.setShutdownOptions(true);
        spiclk.setShutdownOptions(true);
        spimosi.setShutdownOptions(true);
        spimiso.setShutdownOptions(true);

        final GpioPinPwmOutput pwm = gpio.provisionSoftPwmOutputPin(RaspiPin.GPIO_06);
        pwm.setShutdownOptions(true);
        pwm.setPwmRange(100);

        int adcPin0 = 0;

        try {
            while (true) {
                int inputVal0 = readadc(adcPin0, spiclk, spimosi, spimiso, spics);
                int duty = inputVal0 * 100 / 4095;
                pwm.setPwm(duty);
                TimeUnit.MILLISECONDS.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gpio.shutdown();

        return 0;
    }
}
