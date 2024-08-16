package eu.dec21.wp.workitems.mapper;

import eu.dec21.wp.workitems.dto.WorkItemDto;
import eu.dec21.wp.workitems.entity.WorkItem;

public class WorkItemMapper {
    public static WorkItemDto mapToWorkItemDto(WorkItem workItem) {
        return new WorkItemDto(
                workItem.getId(),
                workItem.getName(),
                workItem.getDescription(),
                workItem.getCountry(),
                workItem.getCity(),
                workItem.getAddress(),
                workItem.getAssignee(),
                workItem.getPoints(),
                workItem.getCost(),
                workItem.isBlocked()
        );
    }

    public static WorkItem mapToWorkItem(WorkItemDto workItemDto) {
        return new WorkItem(
                workItemDto.getId(),
                workItemDto.getName(),
                workItemDto.getDescription(),
                workItemDto.getCountry(),
                workItemDto.getCity(),
                workItemDto.getAddress(),
                workItemDto.getAssignee(),
                workItemDto.getPoints(),
                workItemDto.getCost(),
                workItemDto.isBlocked()
        );
    }
}
