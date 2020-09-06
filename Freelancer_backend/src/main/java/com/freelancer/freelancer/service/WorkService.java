package com.freelancer.freelancer.service;

import com.freelancer.freelancer.entity.NeedSkill;
import com.freelancer.freelancer.entity.Work;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkService {

    List<Work> findByTitle(String title);

    Work findByWId(Integer wId);

    Work findByDetails(Integer wId, String keyword, Double paymentHigher, Double paymentLower);

    public Integer save(Work work);

    Page<Work> getWorks(Pageable pageable, String keyword, Double paymentHigher, Double paymentLower);

    Page<Work> getPostedWorks(Integer uId, Pageable pageable, String keyword, Double paymentHigher,
            Double paymentLower);

    boolean changeWorkStatus(Integer u_id, Integer w_id, Integer status);

}
