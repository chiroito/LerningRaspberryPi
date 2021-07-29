package com.example.chapter7;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@QuarkusMain
public class Chapter704CliApplication implements QuarkusApplication {

    private I2CDevice temperatureDevice;

    private static final byte address_adt7410 = 0x48;
    private static final byte register_adt7410 = 0x00;

    private I2CDevice lcdDevice;

    private static final byte address_st7032 = 0x3e;
    private static final byte register_setting = 0x00;
    private static final byte register_display = 0x40;
    private static final int contrast = 32;
    private static final int chars_per_line = 8;
    private static final int display_lines = 2;
    private static final int display_chars = chars_per_line * display_lines;

    private int position;
    private int line;

    private double readAdt7410() throws IOException {

        int word_data = temperatureDevice.read(register_adt7410);
        int data = ((word_data & 0xff00) >> 8) | ((word_data & 0xff) << 8);
        data = data >> 3;
        double temperature = 0.0f;
        if( (data & 0x1000) == 0){
            temperature = ((double)data) * 0.0625f;
        } else {
            temperature = ((double)((~data & 0x1fff) + 1)) * -0.0625f;
        }
        return temperature;
    }

    private void setupSt7032() throws IOException, InterruptedException {
        final int trials = 5;
        for (int i = 0; i < trials; i++) {
            int cLower = (contrast & 0xf);
            int cUpper = (contrast & 0x30) >> 4;
            lcdDevice.write(register_setting, new byte[]{0x38, 0x39, 0x14, (byte) (0x70 | cLower), (byte) (0x54 | cUpper), 0x6c});
            TimeUnit.MILLISECONDS.sleep(200);
            lcdDevice.write(register_setting, new byte[]{0x38, 0x0d, 0x01});
            TimeUnit.MILLISECONDS.sleep(1);
            break;
        }
    }

    private void writeString(String s) throws IOException, InterruptedException {
        for (char c : s.toCharArray()) {
            writeChar(c);
        }
    }

    private void writeChar(char c) throws IOException, InterruptedException {
        char byte_data = checkWritable(c);
        if (position == display_chars) {
            clear();
        } else if (position == chars_per_line * (line + 1)) {
            newLine();
        }
        lcdDevice.write(register_display, (byte) c);
        position++;
    }

    private char checkWritable(char c) {
        if (c >= 0x06 && c <= 0xff) {
            return c;
        } else {
            return 0x20;
        }
    }

    private void clear() throws IOException, InterruptedException {
        position = 0;
        line = 0;
        lcdDevice.write(register_setting, (byte) 0x01);
        TimeUnit.MILLISECONDS.sleep(1);
    }

    private void newLine() throws IOException, InterruptedException {
        if (line == display_lines - 1) {
            clear();
        } else {
            line++;
            position = chars_per_line * line;
            lcdDevice.write(register_setting, (byte) 0xc0);
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }

    @Override
    public int run(String... args) {

        try {
            I2CBus i2cBus = I2CFactory.getInstance(I2CBus.BUS_1);
            this.lcdDevice = i2cBus.getDevice(address_st7032);
            this.temperatureDevice = i2cBus.getDevice(address_adt7410);

            setupSt7032();

            while(true) {
                double inputValue = readAdt7410();
                try {
                    clear();
                    String s = String.valueOf(inputValue);
                    writeString(s);
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.err.println("接続エラースキップ");
                }
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
