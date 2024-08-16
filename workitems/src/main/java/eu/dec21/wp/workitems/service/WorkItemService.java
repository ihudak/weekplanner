package eu.dec21.wp.workitems.service;

import eu.dec21.wp.workitems.dto.WorkItemDto;
import eu.dec21.wp.workitems.dto.WorkItemResponse;

import java.util.List;

public interface WorkItemService {
    WorkItemDto createWorkItem(WorkItemDto workItemDto);
    WorkItemDto getWorkItemById(Long workItemId);
    List<WorkItemDto> getAllWorkItems();
    WorkItemResponse getAllWorkItems(int pageNo, int pageSize);
    WorkItemDto updateWorkItem(Long workItemId, WorkItemDto updatedUser);
    void deleteWorkItem(Long workItemId);

    long count();
}
