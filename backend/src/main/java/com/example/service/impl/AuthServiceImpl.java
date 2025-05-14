package com.example.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.dto.BaseLoginRequest;
import com.example.dto.LoginResponse;
import com.example.entity.Admin;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.entity.Teacher;
import com.example.entity.TeacherAccount;
import com.example.mapper.AdminMapper;
import com.example.mapper.StudentMapper;
import com.example.mapper.StudentAccountMapper;
import com.example.mapper.TeacherMapper;
import com.example.repository.TeacherAccountRepository;
import com.example.service.AuthService;
import com.example.service.StudentService;
import com.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

        private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
        private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d{14}$");
        private static final Pattern TEACHER_ID_PATTERN = Pattern.compile("^\\d{11}$");

        @Autowired
        private AdminMapper adminMapper;

        @Autowired
        private StudentMapper studentMapper;
        
        @Autowired
        private StudentAccountMapper studentAccountMapper;
        
        @Autowired
        private StudentService studentService;

        @Autowired
        private TeacherMapper teacherMapper;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private TeacherAccountRepository teacherAccountRepository;

        @Override
        public Optional<LoginResponse> login(String username, String password) {
                Admin admin = adminMapper.findByUsername(username);
                
                if (admin == null) {
                        return Optional.empty();
                }

                if (!admin.getIsActive()) {
                        return Optional.of(LoginResponse.builder()
                                .message("账号已被禁用")
                                .build());
                }

                if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
                        return Optional.of(LoginResponse.builder()
                                .message("密码错误")
                                .build());
                }

                // 生成JWT令牌
                String token = jwtUtil.generateToken(admin.getId(), "ADMIN");

                // 更新最后登录时间
                adminMapper.updateLastLogin(admin.getId(), LocalDateTime.now());

                return Optional.of(LoginResponse.builder()
                        .message("登录成功")
                        .loginData(LoginResponse.LoginData.builder()
                                .token(token)
                                .role("ADMIN")
                                .build())
                        .userInfo(LoginResponse.UserInfo.builder()
                                .id(admin.getId())
                                .username(admin.getId())
                                .name(admin.getId())
                                .role("ADMIN")
                                .build())
                        .token(token)
                        .username(admin.getId())
                        .role("ADMIN")
                        .build());
        }

        @Override
        public LoginResponse login(BaseLoginRequest request) {
                logger.info("Processing login request for user type: {}", request.getUserType());

                // 验证用户名格式
                if (!validateUsername(request.getUsername(), request.getUserType())) {
                    return LoginResponse.builder()
                            .message("账号格式不正确")
                            .build();
                }

                switch (request.getUserType().toUpperCase()) {
                        case "ADMIN":
                                logger.debug("Handling admin login");
                                return handleAdminLogin(request);
                        case "STUDENT":
                                logger.debug("Handling student login");
                                return handleStudentLogin(request);
                        case "TEACHER":
                                logger.debug("Handling teacher login");
                                return handleTeacherLogin(request);
                        default:
                                logger.warn("Invalid user type: {}", request.getUserType());
                                return LoginResponse.builder()
                                        .message("无效的用户类型")
                                        .build();
                }
        }

        private boolean validateUsername(String username, String userType) {
            if (username == null || username.isEmpty()) {
                return false;
            }

            switch (userType.toUpperCase()) {
                case "STUDENT":
                    return STUDENT_ID_PATTERN.matcher(username).matches();
                case "TEACHER":
                    return TEACHER_ID_PATTERN.matcher(username).matches();
                case "ADMIN":
                    return true; // 管理员账号格式可能有所不同，这里暂时不做特殊验证
                default:
                    return false;
            }
        }

        private LoginResponse handleAdminLogin(BaseLoginRequest request) {
                logger.debug("Looking up admin user: {}", request.getUsername());
                Admin admin = adminMapper.findByUsername(request.getUsername());

                if (admin == null) {
                        logger.warn("Admin user not found: {}", request.getUsername());
                        return LoginResponse.builder()
                                .message("用户名不存在")
                                .build();
                }

                if (!admin.getIsActive()) {
                        logger.warn("Admin account is disabled: {}", request.getUsername());
                        return LoginResponse.builder()
                                .message("账号已被禁用")
                                .build();
                }

                if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
                        logger.warn("Invalid password for admin: {}", request.getUsername());
                        return LoginResponse.builder()
                                .message("密码错误")
                                .build();
                }

                logger.info("Admin login successful: {}", request.getUsername());
                adminMapper.updateLastLogin(admin.getId(), LocalDateTime.now());
                String token = jwtUtil.generateToken(admin.getId(), "ADMIN");

                return LoginResponse.builder()
                        .message("登录成功")
                        .loginData(LoginResponse.LoginData.builder()
                                .token(token)
                                .role("ADMIN")
                                .build())
                        .userInfo(LoginResponse.UserInfo.builder()
                                .id(admin.getId())
                                .username(admin.getId())
                                .name(admin.getId())
                                .role("ADMIN")
                                .build())
                        .token(token)
                        .username(admin.getId())
                        .role("ADMIN")
                        .build();
        }

        private LoginResponse handleStudentLogin(BaseLoginRequest request) {
                // 从学生账号表中查找账号
                StudentAccount studentAccount = studentAccountMapper.findByUsername(request.getUsername());

                if (studentAccount == null) {
                        return LoginResponse.builder()
                                .message("学号不存在")
                                .build();
                }

                if (!studentAccount.getIsActive()) {
                        return LoginResponse.builder()
                                .message("账号已被禁用")
                                .build();
                }

                if (!passwordEncoder.matches(request.getPassword(), studentAccount.getPasswordHash())) {
                        return LoginResponse.builder()
                                .message("密码错误")
                                .build();
                }

                // 获取学生信息
                Student student = studentMapper.selectById(studentAccount.getStudentId());
                
                if (student == null) {
                        return LoginResponse.builder()
                                .message("学生信息不存在")
                                .build();
                }

                // 更新最后登录时间
                studentAccountMapper.updateLastLogin(studentAccount.getStudentId(), LocalDateTime.now());
                
                // 生成JWT令牌
                String token = jwtUtil.generateToken(studentAccount, "STUDENT");

                return LoginResponse.builder()
                        .message("登录成功")
                        .loginData(LoginResponse.LoginData.builder()
                                .token(token)
                                .role("STUDENT")
                                .build())
                        .userInfo(LoginResponse.UserInfo.builder()
                                .id(studentAccount.getStudentId())
                                .username(studentAccount.getUsername())
                                .name(student.getName())
                                .role("STUDENT")
                                .classId(student.getClassId())
                                .majorId(student.getMajorId())
                                .build())
                        .build();
        }

        private LoginResponse handleTeacherLogin(BaseLoginRequest request) {
                logger.info("Handling teacher login for username: {}", request.getUsername());
                
                Teacher teacher = teacherMapper.findByUsername(request.getUsername());
                logger.debug("Teacher lookup result: {}", teacher != null ? "found" : "not found");
                
                if (teacher == null) {
                        logger.warn("Teacher login failed - teacher not found");
                        return LoginResponse.builder()
                                .message("工号不存在")
                                .build();
                }

                // 从 teacher_accounts 表中获取密码哈希
                String passwordHash = teacherMapper.getPasswordHash(request.getUsername());
                Boolean isActive = teacherMapper.getIsActive(request.getUsername());

                if (passwordHash == null || passwordHash.trim().isEmpty()) {
                        logger.warn("Teacher login failed - no password hash found");
                        return LoginResponse.builder()
                                .message("账号未初始化，请联系管理员重置密码")
                                .build();
                }

                if (Boolean.FALSE.equals(isActive)) {
                        logger.warn("Teacher login failed - account is not active");
                        return LoginResponse.builder()
                                .message("账号已被禁用")
                                .build();
                }

                if (!passwordEncoder.matches(request.getPassword(), passwordHash)) {
                        logger.warn("Teacher login failed - invalid password");
                        return LoginResponse.builder()
                                .message("密码错误")
                                .build();
                }

                // 更新最后登录时间
                teacherMapper.updateLastLogin(teacher.getId(), LocalDateTime.now());
                
                String token = jwtUtil.generateToken(teacher, "TEACHER");
                logger.info("Teacher login successful for username: {}", request.getUsername());

                return LoginResponse.builder()
                        .message("登录成功")
                        .loginData(LoginResponse.LoginData.builder()
                                .token(token)
                                .role("TEACHER")
                                .build())
                        .userInfo(LoginResponse.UserInfo.builder()
                                .id(teacher.getId())
                                .username(teacher.getUsername())
                                .name(teacher.getName())
                                .role("TEACHER")
                                .build())
                        .build();
        }
}