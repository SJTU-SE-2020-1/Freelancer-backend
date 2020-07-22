package com.freelancer.freelancer.controller;

import com.freelancer.freelancer.constant.Constant;
import com.freelancer.freelancer.entity.User;
import com.freelancer.freelancer.entity.DoWork;
import com.freelancer.freelancer.entity.Work;
import com.freelancer.freelancer.service.UserService;
import com.freelancer.freelancer.service.WorkService;
import com.freelancer.freelancer.service.DoWorkService;
import com.freelancer.freelancer.utils.msgutils.Msg;
import com.freelancer.freelancer.utils.msgutils.MsgCode;
import com.freelancer.freelancer.utils.msgutils.MsgUtil;
import com.freelancer.freelancer.utils.sessionutils.SessionUtil;
import net.sf.json.JSONObject;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
public class WorkController {

    @Autowired
    private WorkService workService;

    @Autowired
    private UserService userService;

    @Autowired
    private DoWorkService doWorkService;

    private Timestamp String2Date(String str){
        try {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(str);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return timestamp;
        } catch(Exception e) { //this generic but you can control another types of exception
            System.out.println("Error when convert string to date");
            return null;
        }
    }

    @RequestMapping("/getWorkDetail")
    public JSONObject getWorkDetail(@RequestBody Map<String, Integer> params) {
        Integer wId = params.get("w_id");
        Work work = workService.findByWId(wId);
        User postman = userService.findById(work.getUId());

    }

    @RequestMapping("/postWork")
    public void addProject(@RequestBody Map<String, String> params) {
        System.out.println(params.get("title"));
        String name = params.get("title");
        Double paymentLower = Double.parseDouble(params.get("paymentLower"));
        Double paymentHigher = Double.parseDouble(params.get("paymentHigher"));
        String description = params.get("description");
        Timestamp biddingDdl = String2Date(params.get("biddingDdl"));
        Timestamp finishDdl = String2Date(params.get("finishDdl"));
        Integer UId = Integer.parseInt(params.get("uId"));


        Work work = new Work();
        work.setTitle(name);
        work.setUId(UId);
        work.setPaymentLower(paymentLower);
        work.setPaymentHigher(paymentHigher);
        work.setDescription(description);
        work.setBiddingDdl(biddingDdl);
        work.setFinishDdl(finishDdl);
        workService.save(work);
    }

    @RequestMapping("/getWorks")
    public List<Work> getWorks(@RequestBody Map<String, Integer> params) {
        System.out.println("test");
        Integer PageNum = params.get("pagenum");
        Integer PageContentNum = params.get("size");
        if (PageNum <=0 || PageContentNum <=0) { PageNum = 1; PageContentNum = 20; }

        Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "w_id"));
        return workService.getWorks(pageable).getContent();
    }

    @RequestMapping("/getPostedWorks")
    public List<Work> getPostedWorks(@RequestBody Map<String, Integer> params) {
        Integer PageNum = params.get("pagenum");
        Integer PageContentNum = params.get("size");
        Integer uId = params.get("u_id");
        if (PageNum <=0 || PageContentNum <=0) { PageNum = 1; PageContentNum = 20; }

        Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "w_id"));

        return workService.getPostedWorks(uId, pageable).getContent();
    }

    @RequestMapping("/getFinishedWorks")
    public List<Work> getFinishedWorks(@RequestBody Map<String, Integer> params) {
        Integer PageNum = params.get("pagenum");
        Integer PageContentNum = params.get("size");
        Integer uId = params.get("u_id");
        if (PageNum <=0 || PageContentNum <=0) { PageNum = 1; PageContentNum = 20; }

        Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "w_id"));

        List<DoWork> finishedWorks = doWorkService.getWorkerWorks(uId, pageable).getContent();
        List<Work> workerWorks = new ArrayList<Work>();;
        for (DoWork doWork : finishedWorks) {
            workerWorks.add(workService.findByWId(doWork.getW_id()));
        }
        return workerWorks;
    }
}
