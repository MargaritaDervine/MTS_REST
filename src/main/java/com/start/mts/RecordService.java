package com.start.mts;

import com.start.mts.db.RecordRepository;
import com.start.mts.domain.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class RecordService {

    @Autowired
    RecordRepository repository;

    public List<Record> findByCriteria(String filterTicketId,
                                       String filterObjectType,
                                       String filterObjectName,
                                       String filterName,
                                       String filterRefEnv) {
        return repository.findAll(new Specification<Record>() {
            @Override
            public Predicate toPredicate(Root<Record> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (filterTicketId != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("ticketNumber"), filterTicketId)));
                }
                if (filterObjectType != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("objectType"), filterObjectType)));
                }
                if (filterObjectName != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("objectName"), filterObjectName)));
                }
                if (filterName != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("userName"), filterName)));
                }
                if (filterRefEnv != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("referenceEnv"), filterRefEnv)));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }
}

