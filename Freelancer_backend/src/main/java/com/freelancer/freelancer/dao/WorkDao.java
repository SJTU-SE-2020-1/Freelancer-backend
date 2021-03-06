package com.freelancer.freelancer.dao;

import com.freelancer.freelancer.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkDao {
    List<Work> findByTitle(String title);

    Work findByWId(Integer wId);

    Work findByDetails(Integer wId, String keyword, Double paymentHigher, Double paymentLower);

    Integer save(Work work);

    Page<Work> getWorks(Pageable pageable, String keyword, Double paymentHigher, Double paymentLower);

    Page<Work> getPostedWorks(Integer uId, Pageable pageable, String keyword, Double paymentHigher,
            Double paymentLower);

    Page<Work> getReleasedWorks(Integer uId, Pageable pageable, String keyword, Double paymentHigher,
            Double paymentLower);

}
