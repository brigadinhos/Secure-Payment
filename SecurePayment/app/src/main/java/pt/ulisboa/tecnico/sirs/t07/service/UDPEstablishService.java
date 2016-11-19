package pt.ulisboa.tecnico.sirs.t07.service;

import pt.ulisboa.tecnico.sirs.t07.service.dto.OperationData;
import sun.security.x509.IPAddressName;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.PriorityQueue;


/**
 * Created by tiago on 12/11/2016.
 */
public class UDPEstablishService extends AbstractService implements Runnable{

    //private AbstractQueue<DatagramPacket> packets;
    private Integer timeout;
    private DatagramPacket packet;
    private DatagramSocket socket;


    public UDPEstablishService(DatagramSocket socket, DatagramPacket packet, Integer timeout){
        //this.packets = new PriorityQueue<DatagramPacket>();
        this.packet = packet;
        this.timeout = timeout;
        this.socket = socket;

    }

    @Override
    void dispatch() {
        run();
    }

    @Override
    public void run() {
        /*
        * 1 - receber pedido e enviar confirmaçao
        * 2 - receber confirmacao e fazer operacao
        * */

        //this.packet.getData();
        PacketParserService p = null;
        try {
            p = new PacketParserService(this.packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.execute();

        DatagramPacket sendPacket = new DatagramPacket(this.packet.getData(),this.packet.getLength(), this.packet.getAddress(),this.packet.getPort());
        try {
            this.socket.send(sendPacket);
            System.out.println("received");
            /**
             * TODO
             *  aqui deve enviar-se tambem o challenge response
             * */
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[1024];
        DatagramPacket receiveConfirmation = new DatagramPacket(data,data.length);
        try {
            OperationData opData = p.result();
            opData.executeService();


            this.socket.setSoTimeout(this.timeout);
            this.socket.receive(receiveConfirmation);
            this.socket.setSoTimeout(0);
            if(Arrays.equals(receiveConfirmation.getData(),sendPacket.getData())){
                System.out.println("Transaction Confirmed");
                /** TODO
                 * tratar da operacao
                 */
                p.result();

            }else{
                System.out.println("Aborted");
            }
            Arrays.fill( data, (byte) 0 );

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}