package com.freelancer.freelancer.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.freelancer.freelancer.constant.Constant;
import com.freelancer.freelancer.entity.*;
import com.freelancer.freelancer.service.*;
import com.freelancer.freelancer.utils.msgutils.Msg;
import com.freelancer.freelancer.utils.msgutils.MsgCode;
import com.freelancer.freelancer.utils.msgutils.MsgUtil;
import com.freelancer.freelancer.utils.sessionutils.SessionUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.session.Session;
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
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
public class WorkController {

    @Autowired
    private WorkService workService;

    @Autowired
    private UserService userService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private DoWorkService doWorkService;

    @Autowired
    private NeedSkillService needSkillService;

    @Autowired
    private ProposeWorkService proposeWorkService;

    private Timestamp String2Date(String str) {
        try {
            // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(str);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return timestamp;
        } catch (Exception e) { // this generic but you can control another types of exception
            System.out.println("Error when convert string to date");
            return null;
        }
    }

    @RequestMapping("/getWorkDetail")
    public JSONObject getWorkDetail(@RequestBody Map<String, Integer> params) {
        Integer wId = params.get("w_id");
        // JsonConfig jsonConfig = new JsonConfig();
        // jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        Work work = workService.findByWId(wId);
        JSONObject workJson = JSONObject.fromObject(work);
        User postman = userService.findById(work.getU_id());
        postman.setPassword(null);
        JSONObject userJson = JSONObject.fromObject(postman);

        List<Integer> necessarySkillList = needSkillService.getNecessarySkillListByWId(wId);
        List<Skill> necessarySkills = new ArrayList<>();
        for (Integer sId : necessarySkillList) {
            necessarySkills.add(skillService.findById(sId));
        }
        JSONArray necessarySkillJson = JSONArray.fromObject(necessarySkills);

        List<Integer> unnecessarySkillList = needSkillService.getUnnecessarySkillListByWId(wId);
        List<Skill> unnecessarySkills = new ArrayList<>();
        for (Integer sId : unnecessarySkillList) {
            unnecessarySkills.add(skillService.findById(sId));
        }
        JSONArray unnecessarySkillJson = JSONArray.fromObject(unnecessarySkills);

        JSONObject data = new JSONObject();
        data.put("work", workJson);
        data.put("postman", userJson);
        data.put("necessarySkills", necessarySkillJson);
        data.put("unnecessarySkills", unnecessarySkillJson);

        return data;
    }

    @RequestMapping("/postWork")
    public Boolean addProject(@RequestBody Map<String, String> params) {
        System.out.println(params.get("title"));
        String name = params.get("title");
        Double paymentLower = Double.parseDouble(params.get("paymentLower"));
        Double paymentHigher = Double.parseDouble(params.get("paymentHigher"));
        String description = params.get("description");
        Timestamp biddingDdl = String2Date(params.get("biddingDdl"));
        Timestamp finishDdl = String2Date(params.get("finishDdl"));
        Integer UId = Integer.parseInt(params.get("uId"));
        JSONArray skills = JSONArray.fromObject(params.get("needskills"));

        Work work = new Work();
        work.setTitle(name);
        work.setU_id(UId);
        work.setPaymentLower(paymentLower);
        work.setPaymentHigher(paymentHigher);
        work.setDescription(description);
        work.setBiddingDdl(biddingDdl);
        work.setFinishDdl(finishDdl);
        work.setStatus(1);
        Integer w_id = workService.save(work);
        if (skills != null) {
            List<NeedSkill> n_skills = new ArrayList<NeedSkill>();
            for (int i = 0; i < skills.size(); i++) {
                NeedSkill a_skill = new NeedSkill();
                a_skill.setW_id(w_id);
                a_skill.setS_id(skills.getJSONObject(i).getInt("s_id"));
                n_skills.add(a_skill);
            }
            needSkillService.saveNeedSkill(n_skills);
        }
        return true;
    }

    // 0 latest, 1 earliest
    @RequestMapping("/getWorks")
    public List<Work> getWorks(@RequestBody Map<String, String> params) {

        Integer PageNum = Integer.parseInt(params.get("pagenum"));
        Integer PageContentNum = Integer.parseInt(params.get("size"));
        String keyword = params.get("keyword");
        if (keyword == null)
            keyword = "";
        String sort = params.get("sortby");
        Integer sortby = sort != null ? Integer.parseInt(sort) : 0;
        String higher = params.get("paymentHigher");
        String lower = params.get("paymentLower");
        Double paymentHigher = higher != null ? Double.parseDouble(higher) : 10000;
        Double paymentLower = lower != null ? Double.parseDouble(lower) : 0;

        if (PageNum <= 0 || PageContentNum <= 0) {
            PageNum = 1;
            PageContentNum = 20;
        }

        if (sortby == 1) {
            Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "w_id"));
            return workService.getWorks(pageable, keyword, paymentHigher, paymentLower).getContent();
        } else {
            Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.DESC, "w_id"));
            return workService.getWorks(pageable, keyword, paymentHigher, paymentLower).getContent();
        }
    }

    @RequestMapping("/getPostedWorks")
    public List<Work> getPostedWorks(@RequestBody Map<String, String> params) {
        Integer PageNum = Integer.parseInt(params.get("pagenum"));
        Integer PageContentNum = Integer.parseInt(params.get("size"));
        Integer uId = Integer.parseInt(params.get("u_id"));
        if (PageNum <= 0 || PageContentNum <= 0) {
            PageNum = 1;
            PageContentNum = 20;
        }
        String keyword = params.get("keyword");
        if (keyword == null)
            keyword = "";
        String sort = params.get("sortby");
        Integer sortby = sort != null ? Integer.parseInt(sort) : 0;
        String higher = params.get("paymentHigher");
        String lower = params.get("paymentLower");
        Double paymentHigher = higher != null ? Double.parseDouble(higher) : 10000;
        Double paymentLower = lower != null ? Double.parseDouble(lower) : 0;

        if (sortby == 1) {
            Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "w_id"));
            return workService.getPostedWorks(uId, pageable, keyword, paymentHigher, paymentLower).getContent();
        } else {
            Pageable pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.DESC, "w_id"));
            return workService.getPostedWorks(uId, pageable, keyword, paymentHigher, paymentLower).getContent();
        }
    }

    @RequestMapping("/getFinishedWorks")
    public List<Work> getFinishedWorks(@RequestBody Map<String, String> params) {
        Integer PageNum = Integer.parseInt(params.get("pagenum"));
        Integer PageContentNum = Integer.parseInt(params.get("size"));
        Integer uId = Integer.parseInt(params.get("u_id"));
        if (PageNum <= 0 || PageContentNum <= 0) {
            PageNum = 1;
            PageContentNum = 20;
        }
        String keyword = params.get("keyword");
        if (keyword == null)
            keyword = "";
        String sort = params.get("sortby");
        Integer sortby = sort != null ? Integer.parseInt(sort) : 0;
        String higher = params.get("paymentHigher");
        String lower = params.get("paymentLower");
        Double paymentHigher = higher != null ? Double.parseDouble(higher) : 10000;
        Double paymentLower = lower != null ? Double.parseDouble(lower) : 0;

        Pageable pageable;

        if (sortby == 1) {
            pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.ASC, "w_id"));
        } else {
            pageable = PageRequest.of(PageNum - 1, PageContentNum, Sort.by(Sort.Direction.DESC, "w_id"));
        }

        List<DoWork> finishedWorks = doWorkService.getWorkerWorks(uId, pageable).getContent();
        List<Work> workerWorks = new ArrayList<Work>();
        ;
        for (DoWork doWork : finishedWorks) {
            workerWorks.add(workService.findByDetails(doWork.getW_id(), keyword, paymentHigher, paymentLower));
        }
        return workerWorks;
    }

    // admin only?
    @RequestMapping("/changeWorkStatus")
    public boolean changeWorkStatus(@RequestBody Map<String, Integer> params) {
        Integer w_id = params.get("w_id");
        Integer status = params.get("status");

        JSONObject auth = SessionUtil.getAuth();
        Integer u_id = params.get(Constant.USER_ID);
        String u_session_type = auth.getJSONArray("userType").getJSONObject(0).getString("authority");
        if (u_session_type == null) {
            return false;
        }

        if (u_session_type == "ROLE_ADMIN") {
            u_id = -1;

        }

        return workService.changeWorkStatus(u_id, w_id, status);
    }

    @RequestMapping("/applyWork")
    public boolean applyWork(@RequestBody Map<String, String> params) {
        ProposeWork proposeWork = new ProposeWork();
        proposeWork.setUId(Integer.parseInt(params.get("u_id")));
        proposeWork.setWId(Integer.parseInt(params.get("w_id")));
        proposeWork.setExpectPayment(Double.parseDouble(params.get("expect_payment")));
        proposeWork.setRemark(params.get("remark"));
        proposeWorkService.addProposeWork(proposeWork);
        return true;
    }

    @RequestMapping("/cancelApply")
    public boolean cancelApply(@RequestBody Map<String, Integer> params) {
        Integer w_id = params.get("w_id");
        Integer u_id = params.get("u_id");
        ProposeWork proposeWork = proposeWorkService.getPropseWorkByPK(w_id, u_id);
        if (proposeWork == null)
            return false;
        proposeWorkService.delProposeWork(proposeWork);
        return true;
    }
}
