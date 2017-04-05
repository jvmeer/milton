package com.jsvandermeer;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Jacob on 4/4/2017.
 */
public class Livevol {

    Livevol() {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect("ftp.datashop.livevol.com");
            ftpClient.login("jsvmeer@gmail.com", "courageandhonor");
            System.out.println(ftpClient.isConnected());
            for (String name : ftpClient.listNames()) {
                System.out.println(name);
            }
        } catch (IOException exception) {
            exception.printStackTrace();

        }
    }


}
