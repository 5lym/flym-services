package com.flym.services;

import com.flym.services.shared.SharedStruct;
import com.flym.services.tutorial.Calculator;
import com.flym.services.tutorial.InvalidOperation;
import com.flym.services.tutorial.Work;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Created by Administrator on 5/10/2017.
 */
public class Server {

    public static class CalculatorHandler implements Calculator.Iface {

        @Override
        public SharedStruct getStruct(int key) throws TException {
            SharedStruct result = new SharedStruct(1, "1");
            return result;
        }

        @Override
        public void ping() throws TException {
            System.out.println("ping");
        }

        @Override
        public int add(int num1, int num2) throws TException {
            return num1 +  num2;
        }

        @Override
        public int calculate(int logid, Work w) throws InvalidOperation, TException {
            return logid;
        }

        @Override
        public void zip() throws TException {
            System.out.println("zip");
        }
    }

    public static CalculatorHandler handler;

    public static Calculator.Processor processor;

    public static void main(String[] args) {
        try {
            handler = new CalculatorHandler();
            processor = new Calculator.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };
            /*Runnable secure = new Runnable() {
                public void run() {
                    secure(processor);
                }
            };*/

            new Thread(simple).start();
            //new Thread(secure).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(Calculator.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void secure(Calculator.Processor processor) {
        try {
          /*
           * Use TSSLTransportParameters to setup the required SSL parameters. In this example
           * we are setting the keystore and the keystore password. Other things like algorithms,
           * cipher suites, client auth etc can be set.
           */
            TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            // The Keystore contains the private key
            params.setKeyStore("../../lib/java/test/.keystore", "thrift", null, null);

          /*
           * Use any of the TSSLTransportFactory to get a server transport with the appropriate
           * SSL configuration. You can use the default settings if properties are set in the command line.
           * Ex: -Djavax.net.ssl.keyStore=.keystore and -Djavax.net.ssl.keyStorePassword=thrift
           *
           * Note: You need not explicitly call open(). The underlying server socket is bound on return
           * from the factory class.
           */
            TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(9091, 0, null, params);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multi threaded server
            // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the secure server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
