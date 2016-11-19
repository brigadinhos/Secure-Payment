package pt.ulisboa.tecnico.sirs.t07.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Optional;
import java.util.Vector;

/**
 * Created by tiago on 12/11/2016.
 */
public class UDPConnectionSetUp extends AbstractService {

    private DatagramSocket socket;
    private Integer timeout;
    private Integer threadLimit;
    private Integer distributionPosition;
    private Vector<Thread> threadList;

    public UDPConnectionSetUp(Integer timeout, Optional<Integer> port) throws SocketException {
        this.socket = new DatagramSocket(port.orElse(5000));
        this.timeout = timeout;
        this.threadLimit = 1;
        this.distributionPosition = 0;
        this.threadList = new Vector<Thread>();
    }


    @Override
    void dispatch() {
        byte[] data = new byte[120];

        while(true){
            DatagramPacket packet = new DatagramPacket(data,data.length);
            //FIXME
            try {
                this.socket.receive(packet); // Esta a espera de pedidos
            } catch (IOException e) {
                e.printStackTrace();
            }
            UDPEstablishService test =  new UDPEstablishService(this.socket,packet,timeout);
            test.execute();
            // Assim que receber um pedido tem de criar uma thread ou mete em fila numa já existente para processar o pedido
            //if(threadList.size()<threadLimit){
                //Thread thread = new Thread(new UDPEstablishService(this.socket,packet,timeout));
                //threadList.add(thread);
            /*}else{
                Thread t = threadList.get(distributionPosition);//FIXME
            }
            distributionPosition = (distributionPosition+1)%threadLimit;
            */


        }





    }
}