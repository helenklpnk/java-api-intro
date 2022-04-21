package entities;

public class Account {
    String login;
    String passwd;
    String fname;
    String lname;
    String city;
    double balance;

    public Account() {
    }

    public Account(String login, String passwd, String fname, String lname, String city, double balance) {
        this.login = login;
        this.passwd = passwd;
        this.fname = fname;
        this.lname = lname;
        this.city = city;
        this.balance = balance;
    }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public String getPasswd() { return passwd; }

    public void setPasswd(String passwd) { this.passwd = passwd; }

    public String getFname() { return fname; }

    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }

    public void setLname(String lname) { this.lname = lname; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }
}
