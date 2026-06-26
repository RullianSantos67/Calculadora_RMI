package br.edu.ifsuldeminas.mch.sd.rmi.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Operations extends Remote {

    // Operações básicas originais
    Number sum(Number x, Number y) throws RemoteException;
    Number sub(Number x, Number y) throws RemoteException;
    Number mul(Number x, Number y) throws RemoteException;
    Number div(Number x, Number y) throws RemoteException;

    // Novas operações
    Number sqrt(Number x, Number degree) throws RemoteException;        // Raiz (quadrada=2, cúbica=3, etc)
    Number power(Number x, Number exp) throws RemoteException;          // Potência
    Number percentage(Number x, Number total) throws RemoteException;   // Porcentagem (x% de total)
    Number mod(Number x, Number y) throws RemoteException;              // Módulo
    String factorial(Number x) throws RemoteException;                  // Fatorial

    // Conversões
    String decimalToBinary(Number x) throws RemoteException;
    String decimalToHex(Number x) throws RemoteException;
    long binaryToDecimal(String binary) throws RemoteException;
    long hexToDecimal(String hex) throws RemoteException;

    // Histórico
    List<String> lastOperations(int howMany) throws RemoteException;
    List<String> lastOperations() throws RemoteException;
}
