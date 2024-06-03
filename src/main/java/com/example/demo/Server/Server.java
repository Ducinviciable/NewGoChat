package com.example.demo.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import com.example.demo.db.DatabaseManager;

public class Server {
    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    private void createRoom(ServerSocket serverSocket, Socket socket, ArrayList<ClientHandler> clients, int roomId) {
        clients = new ArrayList<ClientHandler>();
        try {
            serverSocket = new ServerSocket(roomId);
            while (true) {
                System.out.println("Room " + roomId + " waiting for clients...");
                socket = serverSocket.accept();
                System.out.println("Connected");
                ClientHandler clientThread = new ClientHandler(socket, clients);
                clients.add(clientThread);
                //clientThread.start();

                // Thread
                ThreadPool.getInstance().getExecutorService().submit(clientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        DatabaseManager.connect();

        ServerSocket serverSocket;
        Socket socket;
        try {
            serverSocket = new ServerSocket(8889);

            while (true) {
                System.out.println("Waiting for clients...");
                socket = serverSocket.accept();
                System.out.println("Connected");
                ClientHandler clientThread = new ClientHandler(socket, clients);
                clients.add(clientThread);
                // Thread
                //clientThread.start();
                ThreadPool.getInstance().getExecutorService().submit(clientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Thread
        finally {
            ThreadPool.getInstance().shutdown();
        }
    }
}