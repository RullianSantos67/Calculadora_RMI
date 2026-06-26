package br.edu.ifsuldeminas.mch.sd.rmi.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import br.edu.ifsuldeminas.mch.sd.rmi.remote.Operations;

public class Server {

    private static final int RMI_PORT = 1099;
    private static Registry registry;

    public Server() {
        try {
            Calculator calculator = new Calculator();

            registry = LocateRegistry.createRegistry(RMI_PORT);

            registry.rebind("CalculatorService", calculator);

            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║   Calculadora RMI - Servidor ativo   ║");
            System.out.println("║   Porta: " + RMI_PORT + "                          ║");
            System.out.println("╚══════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Força o RMI a usar o IP da interface Wi-Fi em vez do Loopback virtual
        System.setProperty("java.rmi.server.hostname", "172.20.10.3");
        new Server();
    }
}
