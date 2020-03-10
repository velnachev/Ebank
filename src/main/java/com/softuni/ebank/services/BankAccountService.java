package com.softuni.ebank.services;

import com.softuni.ebank.bindingModels.BankAccountBindingModel;
import com.softuni.ebank.entities.BankAccount;
import com.softuni.ebank.entities.Transaction;
import com.softuni.ebank.entities.User;
import com.softuni.ebank.repositories.BankAccountRepository;
import com.softuni.ebank.repositories.TransactionRepository;
import com.softuni.ebank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Set<BankAccount> findAllByOwnerUsername(Principal principal) {
        Set<BankAccount> bankAccounts = this.bankAccountRepository
                .findAllByOwnerUsername(principal.getName());

        return bankAccounts;
    }

    public boolean createAccount(BankAccountBindingModel bankAccountBindingModel) {
        if (bankAccountBindingModel.getIban() == null || bankAccountBindingModel.getIban().equals("")) {
            return false;
        }

        BankAccount bankAccount = this.bankAccountRepository.findByIban(bankAccountBindingModel.getIban());
        if (bankAccount != null) {
            return false;
        }
        User user = this.userRepository.findByUsername(bankAccountBindingModel.getUsername());
        if (user == null) {
            return false;
        }

        bankAccount = new BankAccount();
        bankAccount.setOwner(user);
        bankAccount.setIban(bankAccountBindingModel.getIban());
        bankAccount.setBalance(BigDecimal.ZERO);

        this.bankAccountRepository.save(bankAccount);
        return true;
    }

    public BankAccountBindingModel extractAccountForTransaction(Long id) {
        BankAccount bankAccount = this.bankAccountRepository.findById(id).orElse(null);

        if (bankAccount == null) {
            throw new IllegalArgumentException("Invalid Bank Account!");
        }

        BankAccountBindingModel bankAccountBindingModel = new BankAccountBindingModel();
        bankAccountBindingModel.setId(id);
        bankAccountBindingModel.setUsername(bankAccount.getOwner().getUsername());
        bankAccountBindingModel.setIban(bankAccount.getIban());

        return bankAccountBindingModel;
    }

    public boolean depositAmount(BankAccountBindingModel bankAccountBindingModel) {
        BankAccount bankAccount = this.bankAccountRepository.findById(bankAccountBindingModel.getId()).orElse(null);

        if (bankAccount == null) {
            return false;
        } else if (bankAccountBindingModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        bankAccount.setBalance(bankAccount.getBalance().add(bankAccountBindingModel.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setType("DEPOSIT");
        transaction.setFromAccount(bankAccount);
        transaction.setToAccount(bankAccount);
        transaction.setAmount(bankAccountBindingModel.getAmount());

        this.transactionRepository.save(transaction);
        this.bankAccountRepository.save(bankAccount);

        return true;
    }

    public boolean withdrawAmount(BankAccountBindingModel bankAccountBindingModel) {
        BankAccount bankAccount = this.bankAccountRepository.findById(bankAccountBindingModel.getId()).orElse(null);

        if (bankAccount == null) {
            return false;
        } else if (bankAccountBindingModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        } else if (bankAccount.getBalance().compareTo(bankAccountBindingModel.getAmount()) < 0) {
            return false;
        }

        bankAccount.setBalance(bankAccount.getBalance().subtract(bankAccountBindingModel.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setType("WITHDRAW");
        transaction.setFromAccount(bankAccount);
        transaction.setToAccount(bankAccount);
        transaction.setAmount(bankAccountBindingModel.getAmount());

        this.transactionRepository.save(transaction);
        this.bankAccountRepository.save(bankAccount);

        return true;
    }

    public boolean transferAmount(BankAccountBindingModel bankAccountBindingModel) {
        BankAccount fromAccount = this.bankAccountRepository.findById(bankAccountBindingModel.getId()).orElse(null);
        BankAccount toAccount = this.bankAccountRepository.findById(bankAccountBindingModel.getReceiverId()).orElse(null);

        if (fromAccount == null || toAccount == null) {
            return false;
        } else if (bankAccountBindingModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal newBalanceFromAccount = fromAccount.getBalance().subtract(bankAccountBindingModel.getAmount());
        fromAccount.setBalance(newBalanceFromAccount);

        if (fromAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal newBalanceToAccount = toAccount.getBalance().add(bankAccountBindingModel.getAmount());
        toAccount.setBalance(newBalanceToAccount);

        Transaction transaction = new Transaction();
        transaction.setType("TRANSFER");
        transaction.setToAccount(toAccount);
        transaction.setFromAccount(fromAccount);
        transaction.setAmount(bankAccountBindingModel.getAmount());

        this.bankAccountRepository.save(fromAccount);
        this.bankAccountRepository.save(toAccount);
        this.transactionRepository.save(transaction);

        return true;
    }

    public Set<BankAccount> getAllBankAccountsForTransfer(Long id) {
        Set<BankAccount> bankAccounts = this.bankAccountRepository.findAll().stream().filter(ba -> !ba.getId().equals(id)).collect(Collectors.toSet());

        return bankAccounts;
    }
}
