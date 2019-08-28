package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Credits.CreditsLog;
import com.rabbit.backend.Bean.Credits.CreditsLogForm;
import com.rabbit.backend.Bean.Credits.DepositSubmitForm;
import com.rabbit.backend.DAO.CreditsLogDAO;
import com.rabbit.backend.DAO.DepositDAO;
import com.rabbit.backend.DAO.UserDAO;
import com.rabbit.backend.Utilities.CreditsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepositService {
    private CreditsLogDAO creditsLogDAO;
    private DepositDAO depositDAO;
    private UserDAO userDAO;
    private CreditsUtil creditsUtil;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public DepositService(CreditsLogDAO creditsLogDAO, DepositDAO depositDAO, UserDAO userDAO, CreditsUtil creditsUtil) {
        this.creditsLogDAO = creditsLogDAO;
        this.depositDAO = depositDAO;
        this.userDAO = userDAO;
        this.creditsUtil = creditsUtil;
    }

    public List<CreditsLog> creditsLogList(Integer page) {
        return depositDAO.unverifiedDeposit((page - 1) * PAGESIZE, page * PAGESIZE);
    }

    @Transactional
    public void setDeposit(String cid, Integer status) {
        CreditsLog creditsLog = creditsLogDAO.find(cid);
        if (creditsLog.getType().equals("deposit")) {
            if (creditsLog.getStatus() != 1 && status == 1) {
                userDAO.increaseCredits(creditsLog.getUser().getUid(),
                        creditsUtil.getCreditsNameByType(creditsLog.getCreditsType()), creditsLog.getCredits());
            }
            depositDAO.setDeposit(cid, status);
        }
    }

    public String submitDeposit(DepositSubmitForm form, String uid) {
        CreditsLogForm creditsLogForm = new CreditsLogForm();

        creditsLogForm.setCreditsType(3);
        creditsLogForm.setCredits(form.getCredits());
        creditsLogForm.setDescription(form.getDescription());
        creditsLogForm.setUid(uid);
        creditsLogForm.setStatus("0");
        creditsLogForm.setType("deposit");

        creditsLogDAO.insert(creditsLogForm);
        return creditsLogForm.getCid();
    }
}
