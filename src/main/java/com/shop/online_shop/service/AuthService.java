
package com.shop.online_shop.service;

import com.shop.online_shop.dto.RegisterRequest;
import com.shop.online_shop.entity.User;
import com.shop.online_shop.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {

        // 1. Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được đăng ký!");
        }

        // 2. Tạo đối tượng User
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // 3. Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Lưu tài khoản vào database
        return userRepository.save(user);
    }

    public User login(String email, String password) {

        // 1. Tìm tài khoản bằng email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Email hoặc mật khẩu không chính xác!"));

        // 2. Kiểm tra mật khẩu bằng BCrypt
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không chính xác!");
        }

        // 3. Đăng nhập thành công
        return user;
    }
}
