package com.example.TaskManagement.Services;

import com.example.TaskManagement.Repositories.UpdatesRepository;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.dtos.updates.UpdateResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdatesService {
//    public UpdateResponseDTO viewUserUpdates
    private final UpdatesRepository updatesRepository;

    public UpdatesService(UpdatesRepository updatesRepository) {
        this.updatesRepository = updatesRepository;
    }

    public ApiResponse<Page<UpdateResponseDTO>> paginateUpdates(int page , int pageSize){
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<UpdateResponseDTO> updateResponseDTOPage = updatesRepository.findAll(pageable)
                .map(updatedAt -> new UpdateResponseDTO(updatedAt.getUser().getUsername(),updatedAt.getUpdatedDate(),updatedAt.getComment()));
    return new ApiResponse<>(200,"updates found",updateResponseDTOPage);
    }

}
