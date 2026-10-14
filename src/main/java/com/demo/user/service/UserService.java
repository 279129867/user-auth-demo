package com.demo.user.service;

import com.demo.user.common.BizException;
import com.demo.user.entity.User;
import com.demo.user.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 查询用户详情（带 Redis 缓存，命中走缓存，降低 DB 压力） */
    @Cacheable(value = "user", key = "#id")
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BizException(404, "用户不存在"));
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    /** 更新昵称 / 邮箱（仅本人可操作，由 controller 保证 userId 匹配） */
    @Transactional
    public User update(Long id, String nickname, String email) {
        User user = getById(id);
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BizException(404, "用户不存在");
        }
        userRepository.deleteById(id);
    }
}
