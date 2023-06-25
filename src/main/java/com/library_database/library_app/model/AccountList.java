package com.library_database.library_app.model;

import java.util.HashMap;
import java.util.Map;

public class AccountList {
    // HashMap data structure of accounts of key: username and value: account
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
