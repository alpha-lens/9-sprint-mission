package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.CreateUserDto;
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
    private final UserState userState;

    @Override
    public boolean isPresent(String name) {
        return userRepository.userNameToId(name) != null;
    }

    @Override
    public boolean isValid(String password) {
        return userRepository.check(userState.getUserId(), password);
    }

    @Override
    public boolean create(CreateUserDto requestDto) {
        return userRepository.createUser(requestDto);
    }

    /// Update
    @Override
    public boolean update(String reName, String rePassword, String reMail, String rePhoneNumber, String reProfile) {
        UUID userId = userState.getUserId();

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
    public String find(String name) {
        if(userRepository.userNameToId(name) == null)
            throw new NotFound("해당 사용자를 찾지 못했습니다");
        return userRepository.getUser(name);
    }

    @Override
    public List<String> findAll() {
        return userRepository.findAll();
    }

    /// Delete
    @Override
    public boolean delete(UUID id) {
        return userRepository.deleteUser(id);
    }
}
