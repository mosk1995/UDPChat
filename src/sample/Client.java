package sample;

import java.net.InetAddress;

/**
 * Created by Xenomorf on 09.09.2015.
 */
public class Client {
    private InetAddress ip;
    private int port;
    private String nick;

    public Client(InetAddress ip, int port, String nick) {
        this.ip = ip;
        this.port = port;
        this.nick = nick;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
