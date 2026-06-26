package br.edu.ifsuldeminas.mch.sd.rmi.client;

import br.edu.ifsuldeminas.mch.sd.rmi.remote.Operations;

import java.rmi.Naming;
import java.util.List;

public class Client {

    public static void main(String[] args) {
        try {
            Operations calc = (Operations) Naming.lookup("rmi://localhost/CalculatorService");

            System.out.println("=== Operações Básicas ===");
            System.out.println("10 + 5  = " + calc.sum(10, 5));
            System.out.println("10 - 5  = " + calc.sub(10, 5));
            System.out.println("10 * 5  = " + calc.mul(10, 5));
            System.out.println("10 / 4  = " + calc.div(10, 4));

            System.out.println("\n=== Raiz ===");
            System.out.println("√25      = " + calc.sqrt(25, 2));
            System.out.println("∛27      = " + calc.sqrt(27, 3));

            System.out.println("\n=== Potência ===");
            System.out.println("2^10     = " + calc.power(2, 10));
            System.out.println("3^3      = " + calc.power(3, 3));

            System.out.println("\n=== Porcentagem ===");
            System.out.println("15% de 200 = " + calc.percentage(15, 200));

            System.out.println("\n=== Módulo ===");
            System.out.println("17 % 5   = " + calc.mod(17, 5));

            System.out.println("\n=== Fatorial ===");
            System.out.println("10!      = " + calc.factorial(10));
            System.out.println("20!      = " + calc.factorial(20));

            System.out.println("\n=== Conversões ===");
            System.out.println("255 → bin = " + calc.decimalToBinary(255));
            System.out.println("255 → hex = " + calc.decimalToHex(255));
            System.out.println("11111111 → dec = " + calc.binaryToDecimal("11111111"));
            System.out.println("FF → dec  = " + calc.hexToDecimal("FF"));

            System.out.println("\n=== Últimas 5 operações ===");
            List<String> hist = calc.lastOperations(5);
            for (String op : hist) System.out.println("  " + op);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
