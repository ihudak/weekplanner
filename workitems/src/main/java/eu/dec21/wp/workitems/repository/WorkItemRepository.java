package eu.dec21.wp.workitems.repository;

import eu.dec21.wp.workitems.entity.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
public class WorkItemRepository {
    @Value("${workitems.count}")
    private String wrkItemCount;

    private ArrayList<WorkItem> workItems;

    private final Logger logger = LoggerFactory.getLogger(WorkItemRepository.class);

    public WorkItemRepository() {
        int workItemCount;
        try {
            workItemCount = Integer.parseInt(this.wrkItemCount);
        } catch (NumberFormatException e) {
            logger.error("Invalid workitems count value: {}", this.wrkItemCount);
            workItemCount = 100;
        }
        if (logger.isInfoEnabled()) logger.info("Generating {} workitems", workItemCount);
        workItems = new ArrayList<>(workItemCount);
        for (int i = 0; i < workItemCount; i++) {
            WorkItem workItem = WorkItem.generateWorkItem();
            if (logger.isDebugEnabled()) logger.debug("Generating work item {}", workItem.getId());
            workItems.add(WorkItem.generateWorkItem());
        }
        if (logger.isInfoEnabled()) logger.info("{} out of {} WorkItems generated", workItems.size(), workItemCount);
    }

    public WorkItem findById(Long id) {
        if(logger.isInfoEnabled()) logger.info("Getting WorkItem {}", id);
        for (WorkItem workItem : workItems) {
            if (workItem.getId() == id) {
                if(logger.isDebugEnabled()) logger.debug("WorkItem {} has been found", id);
                return workItem;
            }
        }
        if(logger.isWarnEnabled()) logger.warn("WorkItem {} has not been found", id);
        return null;
    }

    public ArrayList<WorkItem> findAll() {
        if(logger.isInfoEnabled()) logger.info("Getting all WorkItems");
        if(workItems.isEmpty() && logger.isWarnEnabled()) logger.warn("No WorkItems found");
        return workItems;
    }

    public WorkItem save(WorkItem workItem) {
        if(logger.isInfoEnabled()) logger.info("Updating WorkItem {}", workItem.getId());
        WorkItem wi = this.findById(workItem.getId());
        if (wi != null) {
            int idx = workItems.indexOf(wi);
            workItems.set(idx, workItem);
            if(logger.isInfoEnabled()) logger.info("WorkItem {} has been updated", workItem.getId());
        } else {
            if (logger.isDebugEnabled()) logger.debug("WorkItem {} did not exist. Creating", workItem.getId());
            workItems.add(workItem);
        }
        return workItem;
    }

    public void deleteById(Long id) {
        if(logger.isInfoEnabled()) {
            logger.info("Deleting WorkItem {}", id);
        }

        if (this.findById(id) != null) {
            this.workItems.remove(this.findById(id));
            if(logger.isDebugEnabled()) {
                logger.debug("WorkItem {} has been deleted", id);
            }
        } else if (logger.isWarnEnabled()) {
            logger.warn("WorkItem with id {} not found", id);
        }
        return;
    }

    public int count() {
        if(logger.isInfoEnabled()) {
            logger.info("Getting the number of WorkItems {}", workItems.size());
            if (workItems.isEmpty()) logger.info("WorkItems are empty");
        }
        return workItems.size();
    }
}
