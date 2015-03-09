
package cn.ljj.main;

import cn.ljj.server.ServerThread;

public class Main {
    public static final String TAG = "Main";

    public static void main(String[] args) {
         ServerThread mServer = new ServerThread();
         new Thread(mServer).start();
         System.out.println("StartupInit init");
    }
}
