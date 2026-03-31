package com.example.TaskManagement.Services;

import com.example.TaskManagement.Entities.Category;
import com.example.TaskManagement.Entities.Task;
import com.example.TaskManagement.Entities.Users;
import com.example.TaskManagement.Mapper.TaskMapper;
import com.example.TaskManagement.Repositories.CategoryRepository;
import com.example.TaskManagement.Repositories.TaskRepository;
import com.example.TaskManagement.dtos.category.CategoryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserServices userServices;
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    public CategoryService(CategoryRepository categoryRepository, UserServices userServices, TaskMapper taskMapper, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.userServices = userServices;
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
    }

    public Category findCategoryByNameOrCreate(@NotBlank @Size(max = 200, message = "categoryName can not exceed 200char") String name) {
        return categoryRepository.findByName(name).orElseGet(() -> categoryRepository.save(new Category(null, name, null)));
    }

    public CategoryDTO createCategory(Long userId, @NotBlank @Size(max = 200, message = "categoryName can not exceed 200char") String name) {
        userServices.getUserEntityById(userId);

        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("such category already exist");
        }
        Category cat=new Category();
        cat.setName(name);
        Category savedCat= categoryRepository.save(cat);
        return new CategoryDTO(savedCat.getId(),savedCat.getName(),savedCat.getTaskList().stream().map(taskMapper::toTaskSummaryDTO).toList());
    }

    public List<CategoryDTO> getAllCategories(Long userId) {
        userServices.getUserEntityById(userId);

        return categoryRepository.findAll().stream().map(category -> new CategoryDTO(category.getId(), category.getName(), category.getTaskList().stream().map(taskMapper::toTaskSummaryDTO).toList())).toList();
    }
public Category getCategoryEntityById(Long id){
        return categoryRepository.findById(id).orElseThrow(()->new RuntimeException("no such category "));
}
    public Category getCategoryEntityByName(String name){
        return categoryRepository.findByName(name).orElseThrow(()->new RuntimeException("no such category "));
    }
    public void deleteCategory(Long userId, Long id) {
        Users user = userServices.getUserEntityById(userId);
        Category category= getCategoryEntityById(id);
        for(Task task:category.getTaskList()){
            task.setCategory(findCategoryByNameOrCreate("General"));
            taskRepository.save(task);
        }
        categoryRepository.deleteById(id);

    }
}
