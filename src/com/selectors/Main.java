package com.selectors;

import com.selectors.TCPReactor;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            TCPReactor reactor = new TCPReactor(2000);
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
