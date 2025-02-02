package eu.dec21.wp.workitems.service.impl;

import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.workitems.dto.WorkItemDto;
import eu.dec21.wp.workitems.dto.WorkItemResponse;
import eu.dec21.wp.workitems.entity.WorkItem;
import eu.dec21.wp.workitems.mapper.WorkItemMapper;
import eu.dec21.wp.workitems.repository.WorkItemRepository;
import eu.dec21.wp.workitems.service.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkItemServiceImpl implements WorkItemService {
    private WorkItemRepository workItemRepository = new WorkItemRepository();


    @Override
    public WorkItemDto createWorkItem(WorkItemDto workItemDto) {
        WorkItem workItem = WorkItemMapper.mapToWorkItem(workItemDto);
        WorkItem savedWorkItem = workItemRepository.save(workItem);
        return WorkItemMapper.mapToWorkItemDto(savedWorkItem);
    }

    @Override
    public WorkItemDto getWorkItemById(Long workItemId) {
        WorkItem workItem = workItemRepository.findById(workItemId);

        if (workItem == null) {
            throw new ResourceNotFoundException("WorkItem with id " + workItemId + " not found");
        }

        return WorkItemMapper.mapToWorkItemDto(workItem);
    }

    @Override
    public List<WorkItemDto> getAllWorkItems() {
        List<WorkItem> workItems = workItemRepository.findAll();
        return workItems.stream().map(WorkItemMapper::mapToWorkItemDto).collect(Collectors.toList());
    }

    @Override
    public WorkItemResponse getAllWorkItems(int pageNo, int pageSize) {
        List<WorkItem> workItemList = workItemRepository.findAll();
        List<WorkItemDto> workItemDtoList = workItemList.stream().map(WorkItemMapper::mapToWorkItemDto).toList();

        WorkItemResponse workItemResponse = new WorkItemResponse();
        workItemResponse.setContent(workItemDtoList);
        workItemResponse.setPageNo(0);
        workItemResponse.setPageSize(workItemRepository.count());
        workItemResponse.setTotalElements(workItemRepository.count());
        workItemResponse.setTotalPages(1);
        workItemResponse.setLast(true);

        return workItemResponse;
    }

    @Override
    public WorkItemDto updateWorkItem(Long workItemId, WorkItemDto updatedWorkItemDto) {
        WorkItem workItem = workItemRepository.findById(workItemId);

        if (workItem == null) {
            throw new ResourceNotFoundException("WorkItem with id " + workItemId + " not found");
        }

        workItem.setName(updatedWorkItemDto.getName());
        workItem.setDescription(updatedWorkItemDto.getDescription());
        workItem.setCountry(updatedWorkItemDto.getCountry());
        workItem.setCity(updatedWorkItemDto.getCity());
        workItem.setAddress(updatedWorkItemDto.getAddress());
        workItem.setAssignee(updatedWorkItemDto.getAssignee());
        workItem.setPoints(updatedWorkItemDto.getPoints());
        workItem.setCost(updatedWorkItemDto.getCost());
        workItem.setBlocked(updatedWorkItemDto.isBlocked());

        WorkItem updatedWorkItem = workItemRepository.save(workItem);

        return WorkItemMapper.mapToWorkItemDto(updatedWorkItem);
    }

    @Override
    public void deleteWorkItem(Long workItemId) {
        WorkItem workItem = workItemRepository.findById(workItemId);

        if (workItem == null) {
            throw new ResourceNotFoundException("WorkItem with id " + workItemId + " not found");
        }

        workItemRepository.deleteById(workItemId);
    }

    @Override
    public long count() {
        return workItemRepository.count();
    }
}
