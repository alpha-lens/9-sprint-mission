package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID userNameToId(String name) {
        return userRepository.userNameToId(name);
    }

    @Override
    public boolean isPresent(UUID id) {
        return userRepository.isPresent(id);
    }

    @Override
    public boolean isInvalid(UUID userId, String password) {
        return userRepository.checkInvalid(userId, password);
    }

    @Override
    public ResponseUserDto create(RequestCreateUserDto requestDto) {
        userRepository.duplicateChecker("사용자명", requestDto.username());
        userRepository.duplicateChecker("이메일", requestDto.email());

        return userRepository.create(requestDto);
    }

    /// Update
    @Override
    public ResponseUserDto update(RequestUpdateUserDto requestDto) {
        userRepository.duplicateChecker("사용자명", requestDto.reName());
        userRepository.duplicateChecker("이메일", requestDto.reMail());
        userRepository.duplicateChecker("전화번호", requestDto.rePhoneNumber());


        UUID reProfileId = requestDto.reProfileId();
        UUID userId = requestDto.id();
        if(reProfileId != null){
            binaryContentRepository.delete(userId);
        }
        return userRepository.update(requestDto);
    }

    /// Read
    @Override
    public ResponseUserDto find(String name) {
        return userRepository.find(name);
    }

    @Override
    public ResponseUserDto find(UUID id) {
        return userRepository.find(id);
    }

    @Override
    public List<ResponseUserDto> findAll() {
        return userRepository.findAll();
    }

    /// Delete
    @Override
    public boolean delete(UUID id) {
        binaryContentRepository.delete(id);
        return userRepository.delete(id);
    }
}
