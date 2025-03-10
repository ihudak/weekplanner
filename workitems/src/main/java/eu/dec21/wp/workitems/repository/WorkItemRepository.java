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

    final private Logger logger = LoggerFactory.getLogger(WorkItemRepository.class);

    public WorkItemRepository() {
        int workItemCount;
        try {
            workItemCount = Integer.parseInt(this.wrkItemCount);
        } catch (NumberFormatException e) {
            workItemCount = 100;
        }
        workItems = new ArrayList<>(workItemCount);
        for (int i = 0; i < workItemCount; i++) {
            workItems.add(WorkItem.generateWorkItem());
        }
    }

    public WorkItem findById(Long id) {
        for (WorkItem workItem : workItems) {
            if (workItem.getId() == id) {
                return workItem;
            }
        }
        return null;
    }

    public ArrayList<WorkItem> findAll() {
        return workItems;
    }

    public WorkItem save(WorkItem workItem) {
        WorkItem wi = this.findById(workItem.getId());
        if (wi != null) {
            int idx = workItems.indexOf(wi);
            workItems.set(idx, workItem);
        } else {
            workItems.add(workItem);
        }
        return workItem;
    }

    public void deleteById(Long id) {
        return;
    }

    public int count() {
        return workItems.size();
    }
}
