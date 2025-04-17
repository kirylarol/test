package by.kirylarol.spendsculptor.dto;

import by.kirylarol.spendsculptor.entities.AccountUser;
import by.kirylarol.spendsculptor.entities.Receipt;

import java.util.List;

public class PartAccountDTO {


    AccountUser accountUser;
    List<Receipt> receiptList;

    public AccountUser getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(AccountUser accountUser) {
        this.accountUser = accountUser;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }
}
