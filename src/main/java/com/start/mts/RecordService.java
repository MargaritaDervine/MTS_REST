package com.start.mts;

import com.start.mts.db.RecordRepository;
import com.start.mts.domain.Record;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.Predicate;
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
        return repository.findAll((Specification<Record>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterTicketId != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("ticketNumber"), filterTicketId)));
            }
            if (filterObjectType != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("objectType"), filterObjectType)));
            }
            if (StringUtils.isNotEmpty(filterObjectName)) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("objectName"), "%" + filterObjectName + "%")));
            }
            if (filterName != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("userName"), filterName)));
            }
            if (filterRefEnv != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("referenceEnv"), filterRefEnv)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        });
    }

    public List<String> getExistingTicketNumbers() {
        List<String> tickets = repository.findDistinctTicketNumbers();
        java.util.Collections.sort(tickets);
        return tickets;
    }

    public List<String> getObjectTypes() {
        List<String> types = repository.findDistinctObjectTypes();
        java.util.Collections.sort(types);
        return types;
    }

    public List<String> getAllEnvironments() {
        List<String> refEnvs = repository.findAllEnvironments();
        java.util.Collections.sort(refEnvs);
        return refEnvs;
    }

    public String highestEnvironmentDeployed(Record record) {
        if (record.isProdDeployed()) {
            return "PROD";
        } else if (record.isAcceptanceDeployed()) {
            return "ACC";
        } else if (record.isSystestDeployed()) {
            return "SYSTEST";
        }
        return "";
    }

    public List<Record> getRecordsForSeveralTickets(String tickets) {
        List<Record> records = new ArrayList<>();
        String[] ids = tickets.split(",");
        for (String id : ids) {
            records.addAll(repository.findByTicketNumber(id.trim()));
        }
        return records;
    }

}

