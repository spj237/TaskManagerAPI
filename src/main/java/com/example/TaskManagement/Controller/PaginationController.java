package com.example.TaskManagement.Controller;

import com.example.TaskManagement.Services.UpdatesService;
import com.example.TaskManagement.Services.UserServices;
import com.example.TaskManagement.dtos.Api.ApiResponse;
import com.example.TaskManagement.dtos.updates.UpdateResponseDTO;
import com.example.TaskManagement.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Api/paginate")
@RequiredArgsConstructor
public class PaginationController {
    private final UpdatesService updatesService;
    @GetMapping("/updates")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<UpdateResponseDTO>> getProductsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam int pageSize) {

        return updatesService.paginateUpdates(page, pageSize);
    }
}
