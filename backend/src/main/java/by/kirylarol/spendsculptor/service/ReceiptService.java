package by.kirylarol.spendsculptor.service;


import by.kirylarol.spendsculptor.entities.Account;
import by.kirylarol.spendsculptor.API.ApiSender;
import by.kirylarol.spendsculptor.API.JsonStringIntoInternalParser;
import by.kirylarol.spendsculptor.entities.Position;
import by.kirylarol.spendsculptor.entities.Receipt;
import by.kirylarol.spendsculptor.repos.ReceiptRepository;
import by.kirylarol.spendsculptor.utils.Hotkeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ReceiptService {

    private final ApiSender apiSender;
    private final JsonStringIntoInternalParser jsonStringIntoInternalParser;
    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(ApiSender apiSender, JsonStringIntoInternalParser jsonStringIntoInternalParser, ReceiptRepository receiptRepository) {
        this.apiSender = apiSender;
        this.jsonStringIntoInternalParser = jsonStringIntoInternalParser;
        this.receiptRepository = receiptRepository;
    }


    public void parseReceipt(MultipartFile receiptImage, Receipt receipt) throws Exception {
        String result = apiSender.send(receiptImage);
        List<Position> positionList = new ArrayList<>(jsonStringIntoInternalParser.firstParseStageAfterHttp(result).parse());
        Optional<Position> position = positionList.stream().filter(elem -> Objects.equals(elem.name(), Hotkeys.TOTAL.getName())).findFirst();
        if (position.isPresent()) {
            BigDecimal total = position.get().getPrice();
            positionList.remove(position.get());
            receipt.setTotal(total);
        }
        receipt.setPositionList(positionList);
    }

    public Receipt save(Receipt receipt) {
        BigDecimal currentTotal = BigDecimal.valueOf(0);
        for (var elem : receipt.getPositionList()) {
            elem.setReceipt(receipt);
            currentTotal = currentTotal.add(elem.getPrice());
        }
        if (receipt.getTotal() == null) {
            receipt.setTotal(currentTotal);
        }
        return receiptRepository.save(receipt);
    }

    @Transactional
    public Receipt addReceipt(Receipt receipt) {
        return this.save(receipt);
    }

    public Receipt getReceipt(int id){
        Optional<Receipt> result = receiptRepository.findById(id);
        return result.orElse(null);
    }

    public List<Receipt> getAllReceipts(int id){
        return receiptRepository.findAll();
    }

    public List<Receipt> getAllByAccount(int id){
        return  receiptRepository.findAllByAccountId(id);
    }

    public List<Receipt> getAllBetween (LocalDate date1, LocalDate date2){
        return receiptRepository.findAllByDateBetween(date1,date2);
    }

    @Transactional
    public void update (Receipt receipt) {
        receiptRepository.save(receipt);
    }

    @Transactional
    public void delete (int id){
        receiptRepository.deleteById(id);
    }


    public BigDecimal getAllSpends (Account account, LocalDate start, LocalDate end){
        return receiptRepository.getTotalByAccount(account,start,end);
    }

    public List<Receipt> getAllReceiptsForUser (int userid){
        return receiptRepository.getReceiptsByUserId(userid);
    }


}
