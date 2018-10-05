import java.net.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;

public class BankAPI
{
    public static void main(String args[]) 
    {
        ServerSocket echoServer = null;
        BufferedReader is; // Server Stream
        PrintStream os; // Client Stream
        Socket clientSocket = null;
        String line = ""; // Request goes here, expects request all on one line
        ArrayList<Account> acc = new ArrayList<Account>(); // Account information stored here
        
        // Setup server at localhost, listen on port 5000
        try {
            echoServer = new ServerSocket(5000);
        } catch (IOException e) {
            System.err.println(e);
        }   
        try {
            clientSocket = echoServer.accept();
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            line = is.readLine();
            System.out.println(line);
            os.println(parse(acc, line)); // Parse request from client and return response
        }   
        catch (IOException e) {
            System.err.println(e);
        }
    
        // I/O is finished, print all account details for debugging purposes
        System.out.println("Finished IO\nCurrent accounts:\n");
        for(int i = 0; i < acc.size(); i++) {
            System.out.println("Customer ID : " + acc.get(i).customerID);
            System.out.println("Account No  : " + acc.get(i).accountNo);
            System.out.println("Balance     : " + acc.get(i).balance);
            System.out.println("\n");
        }
    }

    public static String parse(ArrayList<Account> acc, String line)
    // Parses string from client, processes request and returns HTTP response
    // Empty string is returned if an error occurs
    {
        StringTokenizer token, subToken;
        String method = "";
        Integer customerID = 0;
        Integer accountNo = 0;
        Integer balance = 0;
        Account tempAccount;

        // String is split into tokens with space as the deliminator, it expects the following format:
        // [HTTP METHOD] /api/accounts/[customerID] HTTP/1.1 accountNo [accountNo] balance [balance]

        // Evaluate: [Method]
        token = new StringTokenizer(line, " ", false);
        switch(token.nextToken()) {
            case "POST":    method = "POST";
            break;
            case "DELETE":  method = "DELETE";
            break;
            case "GET":     method = "GET";
            break;
            case "PUT":     method = "PUT";
            break;
            default:        return "HTTP/1.1 400 BAD_REQUEST";
        }

        // Evaluate: /api/accounts/[customerID] and extract [customerID]
        // This token is split into tokens again
        subToken = new StringTokenizer(token.nextToken(), "/", false);

        if(!subToken.nextToken().equals("api")) {
            System.err.println("Error: Unknown URL");
            return "HTTP/1.1 400 BAD_REQUEST";
        }
        if(!subToken.nextToken().equals("accounts")) {
            System.err.println("Error: Unknown URL");
            return "HTTP/1.1 400 BAD_REQUEST\r\n";
        }

        customerID = Integer.valueOf(subToken.nextToken());


        // Assert HTTP/1.1
        if(!token.nextToken().equals("HTTP/1.1")) {
            System.err.println("Error: Incorrect HTTP version");
        }

        // Assert accountNo is present
        if(!token.nextToken().equals("accountNo")) {
            System.err.println("Error: accountNo not specified");
            return "HTTP/1.1 400 BAD_REQUEST\r\n";
        }
        // Evaluate: accountNo
        accountNo = Integer.valueOf(token.nextToken());

        // Assert balance
        if(!token.nextToken().equals("balance")) {
            System.err.println("Error: balance not specified");
            return "HTTP/1.1 400 BAD_REQUEST\r\n";
        }
        // EvaluateL balance
        balance = Integer.valueOf(token.nextToken());

        // Take specified CRUD operation
        switch(method) {
            case "POST": // Create
                tempAccount = new Account(customerID, accountNo, balance);
                acc.add(tempAccount);

            return "HTTP/1.1 200 OK";

            case "GET": // Read
                for(int i = 0; i < acc.size(); i++) {
                    if(acc.get(i).customerID == customerID) {
                        accountNo = acc.get(i).accountNo;
                        balance = acc.get(i).balance;
                    }
                }
            return "HTTP/1.1 200 OK\r\n accountNo " + accountNo + " balance " + balance;

            case "PUT": // Update
                for(int i = 0; i < acc.size(); i++) {
                    if(acc.get(i).customerID == customerID) {
                        acc.get(i).balance = balance;
                    }
                }
            return "HTTP/1.1 200 OK\r\n";

            case "DELETE": // Delete
                for(int i = 0; i < acc.size(); i++) {
                    if(acc.get(i).customerID == customerID) {
                        acc.remove(i);
                    }
                }
            return "HTTP/1.1 200 OK\r\n";
        }

        return "HTTP/1.1 400 BAD_REQUEST\r\n";
    }
}