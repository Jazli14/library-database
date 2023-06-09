package com.example.librarydatabase.Model;

import java.util.HashMap;
import java.util.Map;

public class AccountList {
    private final Map<String, Account> accounts = new HashMap<>();
    public void add(Account account) {
        accounts.put(account.getUsername(), account);
    }

    public Account getAccount(String username) {
        return accounts.get(username);
    }
    public Account removeAccount(String username) {
        return accounts.remove(username);
    }

    public boolean accountExists(String username) {
        return accounts.containsKey(username);
    }
    public Map<String, Account> getAccounts(){ return accounts; }

    public void clearAccounts() { accounts.clear();}
}
