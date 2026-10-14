package com.demo.user.controller;

import com.demo.user.common.ApiResponse;
import com.demo.user.common.BizException;
import com.demo.user.entity.User;
import com.demo.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 查询当前登录用户自己的信息 */
    @GetMapping("/me")
    public ApiResponse<User> me(Authentication authentication) {
        return ApiResponse.success(userService.getById(currentUserId(authentication)));
    }

    /** 查询用户列表（需登录） */
    @GetMapping
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.listAll());
    }

    /** 更新自己的昵称/邮箱 */
    @PutMapping("/me")
    public ApiResponse<User> update(Authentication authentication, @RequestBody Map<String, String> body) {
        Long id = currentUserId(authentication);
        return ApiResponse.success(userService.update(id, body.get("nickname"), body.get("email")));
    }

    /** 删除自己的账号 */
    @DeleteMapping("/me")
    public ApiResponse<Void> delete(Authentication authentication) {
        userService.delete(currentUserId(authentication));
        return ApiResponse.success();
    }

    private Long currentUserId(Authentication authentication) {
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (principal instanceof Long id) {
            return id;
        }
        throw new BizException(401, "未登录或登录已过期");
    }
}
