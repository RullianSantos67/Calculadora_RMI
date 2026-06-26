package br.edu.ifsuldeminas.mch.sd.rmi.server;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.ifsuldeminas.mch.sd.rmi.remote.Operations;

public class Calculator extends UnicastRemoteObject implements Operations {

    private List<String> lastOperations = new ArrayList<>();

    public Calculator() throws RemoteException {
        super();
    }

    // ─── Operações básicas ────────────────────────────────────────────────────

    @Override
    public Number sum(Number x, Number y) {
        Number result = x.doubleValue() + y.doubleValue();
        log(x + " + " + y + " = " + result);
        return result;
    }

    @Override
    public Number sub(Number x, Number y) {
        Number result = x.doubleValue() - y.doubleValue();
        log(x + " - " + y + " = " + result);
        return result;
    }

    @Override
    public Number mul(Number x, Number y) {
        Number result = x.doubleValue() * y.doubleValue();
        log(x + " * " + y + " = " + result);
        return result;
    }

    @Override
    public Number div(Number x, Number y) {
        if (y.doubleValue() == 0) {
            log(x + " / " + y + " = ERROR (divisão por zero)");
            return Double.NaN;
        }
        Number result = x.doubleValue() / y.doubleValue();
        log(x + " / " + y + " = " + result);
        return result;
    }

    // ─── Novas operações ──────────────────────────────────────────────────────

    @Override
    public Number sqrt(Number x, Number degree) {
        if (x.doubleValue() < 0 && degree.intValue() % 2 == 0) {
            log("raiz_" + degree + "(" + x + ") = ERROR (raiz par de número negativo)");
            return Double.NaN;
        }
        double result;
        if (x.doubleValue() < 0) {
            result = -Math.pow(-x.doubleValue(), 1.0 / degree.doubleValue());
        } else {
            result = Math.pow(x.doubleValue(), 1.0 / degree.doubleValue());
        }
        log("raiz_" + degree + "(" + x + ") = " + result);
        return result;
    }

    @Override
    public Number power(Number x, Number exp) {
        Number result = Math.pow(x.doubleValue(), exp.doubleValue());
        log(x + " ^ " + exp + " = " + result);
        return result;
    }

    @Override
    public Number percentage(Number x, Number total) {
        Number result = (x.doubleValue() / 100.0) * total.doubleValue();
        log(x + "% de " + total + " = " + result);
        return result;
    }

    @Override
    public Number mod(Number x, Number y) {
        if (y.doubleValue() == 0) {
            log(x + " % " + y + " = ERROR (módulo por zero)");
            return Double.NaN;
        }
        Number result = x.doubleValue() % y.doubleValue();
        log(x + " % " + y + " = " + result);
        return result;
    }

    @Override
    public String factorial(Number x) {
        long n = x.longValue();
        if (n < 0) {
            log(x + "! = ERROR (fatorial de número negativo)");
            return "ERROR: fatorial de número negativo";
        }
        BigInteger result = BigInteger.ONE;
        for (long i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        log(x + "! = " + result);
        return result.toString();
    }

    // ─── Conversões ───────────────────────────────────────────────────────────

    @Override
    public String decimalToBinary(Number x) {
        long val = x.longValue();
        String result = Long.toBinaryString(val);
        log("dec2bin(" + val + ") = " + result);
        return result;
    }

    @Override
    public String decimalToHex(Number x) {
        long val = x.longValue();
        String result = Long.toHexString(val).toUpperCase();
        log("dec2hex(" + val + ") = " + result);
        return result;
    }

    @Override
    public long binaryToDecimal(String binary) {
        long result = Long.parseLong(binary, 2);
        log("bin2dec(" + binary + ") = " + result);
        return result;
    }

    @Override
    public long hexToDecimal(String hex) {
        long result = Long.parseLong(hex, 16);
        log("hex2dec(" + hex + ") = " + result);
        return result;
    }

    // ─── Histórico ────────────────────────────────────────────────────────────

    @Override
    public List<String> lastOperations(int howMany) {
        if (lastOperations.size() < howMany) return lastOperations();
        return new ArrayList<>(
            lastOperations.subList(lastOperations.size() - howMany, lastOperations.size())
        );
    }

    @Override
    public List<String> lastOperations() {
        return new ArrayList<>(lastOperations);
    }

    // ─── Log interno ─────────────────────────────────────────────────────────

    private void log(String operation) {
        lastOperations.add(operation);
        System.out.printf("[%s] %s%n", new Date(), operation);
    }
}
