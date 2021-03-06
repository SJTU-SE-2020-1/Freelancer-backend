package com.freelancer.freelancer.daoimpl;

import com.freelancer.freelancer.dao.WorkDao;
import com.freelancer.freelancer.entity.Work;
import com.freelancer.freelancer.entity.WorkEnclosure;
import com.freelancer.freelancer.repository.WorkEnclosureRepository;
import com.freelancer.freelancer.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class WorkDaoImpl implements WorkDao {

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private WorkEnclosureRepository workEnclosureRepository;

    @Override
    public List<Work> findByTitle(String title) {
        List<Work> works = workRepository.findByTitle(title);
        for (Work work : works) {
            Optional<WorkEnclosure> workEnclosure = workEnclosureRepository.findById(work.getW_id());
            if (workEnclosure.isPresent()) {
                work.setDescription(workEnclosure.get().getDescription());
            } else {
                work.setDescription(null);
                System.out.println("It's Null");
            }
        }
        return works;
    }

    @Override
    public Work findByWId(Integer wId) {
        Work work = workRepository.getAWork(wId);
        Optional<WorkEnclosure> workEnclosure = workEnclosureRepository.findById(wId);
        if (workEnclosure.isPresent()) {
            work.setDescription(workEnclosure.get().getDescription());
        } else {
            work.setDescription(null);
            System.out.println("It's Null" + wId);
        }
        return work;
    }

    @Override
    public Work findByDetails(Integer wId, String keyword, Double paymentHigher, Double paymentLower) {
        Work work = workRepository.findByDetails(wId, keyword, paymentHigher, paymentLower);
        Optional<WorkEnclosure> workEnclosure = workEnclosureRepository.findById(wId);
        if (workEnclosure.isPresent()) {
            work.setDescription(workEnclosure.get().getDescription());
        } else {
            work.setDescription(null);
            System.out.println("It's Null" + wId);
        }
        return work;
    }

    @Override
    public Integer save(Work work) {

        Work w = workRepository.save(work);
        WorkEnclosure workEnclosure = new WorkEnclosure(w.getW_id(), w.getDescription());
        workEnclosureRepository.save(workEnclosure);
        return w.getW_id();
    }

    @Override
    public Page<Work> getWorks(Pageable pageable, String keyword, Double paymentHigher, Double paymentLower) {
        Page<Work> works = workRepository.getWorks(keyword, paymentHigher, paymentLower, pageable);
        for (Work work : works) {
            Optional<WorkEnclosure> workEnclosure = workEnclosureRepository.findById(work.getW_id());
            if (workEnclosure.isPresent()) {
                work.setDescription(workEnclosure.get().getDescription());
            } else {
                work.setDescription(null);
                System.out.println("It's Null");
            }
        }
        return works;
    }

    @Override
    public Page<Work> getPostedWorks(Integer uId, Pageable pageable, String keyword, Double paymentHigher,
            Double paymentLower) {
        Page<Work> postedWorks = workRepository.getMyPosted(uId, keyword, paymentHigher, paymentLower, pageable);
        for (Work work : postedWorks) {
            Optional<WorkEnclosure> workEnclosure = workEnclosureRepository.findById(work.getW_id());
            if (workEnclosure.isPresent()) {
                work.setDescription(workEnclosure.get().getDescription());
            } else {
                work.setDescription(null);
                System.out.println("It's Null");
            }
        }
        return postedWorks;
    }

    @Override
    public Page<Work> getReleasedWorks(Integer uId, Pageable pageable, String keyword, Double paymentHigher,
            Double paymentLower) {
        Page<Work> releasedWorks = workRepository.getMyRelease(uId, keyword, paymentHigher, paymentLower, pageable);
        for (Work work : releasedWorks) {
            Optional<WorkEnclosure> workEnclosure = workEnclosureRepository.findById(work.getW_id());
            if (workEnclosure.isPresent()) {
                work.setDescription(workEnclosure.get().getDescription());
            } else {
                work.setDescription(null);
                System.out.println("It's Null");
            }
        }
        return releasedWorks;
    }
}
