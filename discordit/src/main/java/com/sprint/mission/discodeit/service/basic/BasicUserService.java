package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final FileUserRepository userRepository;
    private final FileBinaryContentRepository binaryContentRepository;

    public UUID userNameToId(String name) {
        return userRepository.userNameToId(name);
    }

    @Override
    public boolean isPresent(String name) {
        return userRepository.userNameToId(name) != null;
    }

    @Override
    public boolean isValid(UUID userId, String password) {
        return userRepository.check(userId, password);
    }

    @Override
    public boolean create(CreateUserDto requestDto) {
        return userRepository.createUser(requestDto);
    }

    /// Update
    @Override
    public boolean update(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber, String reProfile) {

        if(userRepository.updateUser(userId, reName, rePassword, reMail, rePhoneNumber)) {
            if(reProfile != null){
                binaryContentRepository.delete(AttachmentType.USER, userId);
                binaryContentRepository.create(AttachmentType.USER, userId, reProfile);
            }
            return true;
        }
        return false;
    }

    /// Read
    @Override
    public UserFinder find(String name) {
        if(userRepository.userNameToId(name) == null)
            throw new NotFound("해당 사용자를 찾지 못했습니다");
        return userRepository.find(name);
    }

    @Override
    public List<UserFinder> findAll() {
        return userRepository.findAll();
    }

    /// Delete
    @Override
    public boolean delete(UUID id) {
        return userRepository.deleteUser(id);
    }
}
