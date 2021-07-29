package com.example.chapter7;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter701CliApplication implements QuarkusApplication {

    private I2CDevice device;

    private static final byte address_adt7410 = 0x48;
    private static final byte register_adt7410 = 0x00;

    private double readAdt7410() throws IOException {
        int word_data = device.read(register_adt7410);
        int data = ((word_data & 0xff00) >> 8) | (word_data & 0xff << 8);
        data = data >> 3;
        double temperature = 0;
        if( (data & 0x1000) == 0){
            temperature = data * 0.0625;
        } else {
            temperature = ((~data & 0x1fff) + 1) * -0.0625;
        }
        return temperature;
    }

    @Override
    public int run(String... args) {

        try{
            I2CBus i2cBus = I2CFactory.getInstance(I2CBus.BUS_0);
            this.device = i2cBus.getDevice(address_adt7410);

            while(true) {
                double inputValue = readAdt7410();
                System.out.println(inputValue);
                TimeUnit.MILLISECONDS.sleep(500);
            }

        } catch (I2CFactory.UnsupportedBusNumberException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
