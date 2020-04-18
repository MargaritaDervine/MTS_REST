package com.start.mts;

import com.start.mts.db.EnvironmentRepository;
import com.start.mts.db.NameRepository;
import com.start.mts.db.ObjectTypeRepository;
import com.start.mts.db.RecordRepository;
import com.start.mts.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Component
public class RecordService {

    public static final String COMMA = ",";
    public static final String EMPTY = " ";
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    EnvironmentRepository environmentRepository;
    @Autowired
    NameRepository nameRepository;
    @Autowired
    ObjectTypeRepository objectTypeRepository;

    public List<Record> findByCriteria(String filterTicketId,
                                       String filterObjectType,
                                       String filterObjectName,
                                       String filterName,
                                       String filterRefEnv) {
        return recordRepository.findAll((Specification<Record>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterTicketId != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("ticketNumber"), filterTicketId)));
            }
            if (filterObjectType != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("objectType"), new ObjectType(filterObjectType))));
            }
            if (StringUtils.isNotEmpty(filterObjectName)) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("objectName"), "%" + filterObjectName + "%")));
            }
            if (filterName != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("userName"), new Name(filterName))));
            }
            if (filterRefEnv != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("referenceEnvironment"), new Environment(filterRefEnv, true))));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        });
    }

    public List<String> getExistingTicketNumbers() {
        List<String> tickets = recordRepository.findDistinctTicketNumbers();
        java.util.Collections.sort(tickets);
        return tickets;
    }

    public List<ObjectType> getSortedObjectTypes() {
        List<ObjectType> types = objectTypeRepository.findAll();
        types.sort(Comparator.comparing(ObjectType::getType));
        return types;
    }

    public List<Name> getSortedNames() {
        List<Name> names = nameRepository.findAll();
        names.sort(Comparator.comparing(Name::getUserName));
        return names;
    }

    public List<Environment> getSortedRefEnvironments() {
        List<Environment> refEnvs = environmentRepository.findAllByIsReferenceEnvironment(true);
        refEnvs.sort(Comparator.comparing(Environment::getEnvironmentName));
        return refEnvs;
    }

    public List<Record> getRecordsForSeveralTickets(String tickets, String separator) {
        List<Record> records = new ArrayList<>();
        String[] ids = tickets.split(separator);
        for (String id : ids) {
            records.addAll(recordRepository.findByTicketNumber(id.trim()));
        }
        return records;
    }

    public Record createNewRecord(String ticketNumber, String objectName, String action, String nameStr, String refEnvStr, String objectTypeStr) {
        Name name = nameRepository.getOne(nameStr);
        Environment refEnv = environmentRepository.getOne(refEnvStr);
        ObjectType objectType = objectTypeRepository.getOne(objectTypeStr);
        return new Record(name, refEnv, ticketNumber.toUpperCase(), objectType, objectName, Actions.valueOf(action), null);
    }

}

