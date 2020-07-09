package com.freelancer.freelancer.service;

import com.freelancer.freelancer.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface WorkService {
    public Work findByTitle(String title);

    public void save(Work work);

    Page<Work> getWorks(Pageable pageable);

    Page<Work> getPostedWorks(Integer uId, Pageable pageable);
}
