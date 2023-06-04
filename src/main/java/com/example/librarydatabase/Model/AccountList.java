package com.example.librarydatabase.Model;

import java.util.HashMap;
import java.util.Map;

public class AccountList {
    private Map<String, Member> accounts = new HashMap<>();
    public void add(Member member) {
        accounts.put(member.getUsername(), member);
    }

    public Member getMember(String username) {
        return accounts.get(username);
    }

    public boolean memberExists(String username) {
        return accounts.containsKey(username);
    }
}
