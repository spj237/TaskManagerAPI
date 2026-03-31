package com.example.TaskManagement.Controller;

import com.example.TaskManagement.Services.CategoryService;
import com.example.TaskManagement.dtos.category.CategoryDTO;
import com.example.TaskManagement.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Api/Category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/createCat")
            public ResponseEntity<CategoryDTO> createCat(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody String name){
            return     ResponseEntity.status(HttpStatusCode.valueOf(201)).body(categoryService.createCategory(userDetails.getId(), name));
            }
            @GetMapping("getAllCat")
    public ResponseEntity<List<CategoryDTO>> getAllCat(@AuthenticationPrincipal CustomUserDetails userDetails){
        return  ResponseEntity.ok(categoryService.getAllCategories(userDetails.getId()));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCat(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id){
        categoryService.deleteCategory(userDetails.getId(),id);
        return  ResponseEntity.ok("deleted");
    }

}
