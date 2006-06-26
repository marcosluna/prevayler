// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.demos.demo2.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Account implements java.io.Serializable {

    private static final long serialVersionUID = 3998522662411373397L;

    private long number;

    private String holder;

    private long balance = 0;

    private List<AccountEntry> transactionHistory = new ArrayList<AccountEntry>();

    private transient Set<AccountListener> listeners;

    private Account() {
    }

    Account(long number, String holder) throws InvalidHolder {
        this.number = number;
        setHolder(holder);
    }

    public long number() {
        return number;
    }

    @Override public String toString() {
        // Returns something like "00123 - John Smith"
        return numberString() + " - " + holder;
    }

    public String numberString() {
        return numberString(number);
    }

    static String numberString(long number) {
        return (new java.text.DecimalFormat("00000").format(number));
    }

    public String holder() {
        return holder;
    }

    public void setHolder(String holder) throws InvalidHolder {
        verify(holder);
        this.holder = holder;
        notifyListeners();
    }

    public long balance() {
        return balance;
    }

    public void deposit(long amount, Date timestamp) throws InvalidAmount {
        verify(amount);
        register(amount, timestamp);
    }

    public void withdraw(long amount, Date timestamp) throws InvalidAmount {
        verify(amount);
        register(-amount, timestamp);
    }

    private void register(long amount, Date timestamp) {
        balance += amount;
        transactionHistory.add(new AccountEntry(amount, timestamp));
        notifyListeners();
    }

    private void verify(long amount) throws InvalidAmount {
        if (amount <= 0)
            throw new InvalidAmount("Amount must be greater than zero.");
        if (amount > 10000)
            throw new InvalidAmount("Amount maximum (10000) exceeded.");
    }

    public List transactionHistory() {
        return transactionHistory;
    }

    public void addAccountListener(AccountListener listener) {
        listeners().add(listener);
    }

    public void removeAccountListener(AccountListener listener) {
        listeners().remove(listener);
    }

    private Set<AccountListener> listeners() {
        if (listeners == null)
            listeners = new HashSet<AccountListener>();
        return listeners;
    }

    private void notifyListeners() {
        Iterator it = listeners().iterator();
        while (it.hasNext()) {
            ((AccountListener) it.next()).accountChanged();
        }
    }

    public class InvalidAmount extends Exception {
        private static final long serialVersionUID = 3343517565045905857L;

        InvalidAmount(String message) {
            super(message);
        }
    }

    private static void verify(String newHolder) throws InvalidHolder {
        if (newHolder == null || newHolder.equals("")) {
            throw new InvalidHolder();
        }
    }

    public static class InvalidHolder extends Exception {
        private static final long serialVersionUID = -3234126892127577122L;

        InvalidHolder() {
            super("Invalid holder name.");
        }
    }
}