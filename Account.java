public class Account {
    public Integer customerID;
    public Integer accountNo;
    public Integer balance;

    public Account() {
        customerID = 0;
        accountNo = 0;
        balance = 0;
    }

    public Account(Integer inCustomerID, Integer inAccountNo, Integer inBalance) {
        customerID = inCustomerID;
        accountNo = inAccountNo;
        balance = inBalance;
    }
}