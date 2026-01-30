package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class FileChannelRepository implements ChannelRepository {
    private final Map<String, Channel> publicChannelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> publicChannelIdMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> privateChannelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> privateChannelIdMap = new ConcurrentHashMap<>();
    private final UserState userState;
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if(Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(channel -> {
                        if (channel.getChannelType() == ChannelType.PRIVATE) {
                            privateChannelNameMap.put(channel.getName(), channel);
                            privateChannelIdMap.put(channel.getId(), channel);
                        } else {
                            publicChannelNameMap.put(channel.getName(), channel);
                            publicChannelIdMap.put(channel.getId(), channel);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /// interface
    @Override
    public boolean save(Channel channel) {
        // 입력값: 현재 채널, 변경할 이름
        // TODO: 채널명 변경, 기존 키 삭제 및 추가

        Path path = resolvePath(channel.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();
            if (channel.getChannelType() == ChannelType.PUBLIC) {
                publicChannelNameMap.getOrDefault(channel.getName(),
                    publicChannelNameMap.put(channel.getName(), channel));
                publicChannelIdMap.getOrDefault(channel.getId(),
                    publicChannelIdMap.put(channel.getId(), channel));
            } else {
                privateChannelIdMap.getOrDefault(channel.getId(),
                        privateChannelIdMap.put(channel.getId(), channel));
                privateChannelNameMap.getOrDefault(channel.getName(),
                        privateChannelNameMap.put(channel.getName(), channel));
            }

            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] " + e);
        }
        return false;
    }

    @Override
    public boolean save(String oldName, String newName) {
        Channel channel = publicChannelNameMap.getOrDefault(oldName, null);
        boolean isPrivate = false;

        if (channel == null) {
            channel = privateChannelNameMap.getOrDefault(oldName, null);
            isPrivate = true;
        }

        if (channel == null) return false;

        Path path = resolvePath(publicChannelNameMap.get(oldName).getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();

            if (!isPrivate) {
                publicChannelNameMap.put(newName, channel);
                publicChannelNameMap.remove(oldName);
            } else {
                privateChannelNameMap.put(newName, channel);
                privateChannelNameMap.remove(oldName);
            }

            channel.channelUpdater(newName);

            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] " + e);
            return false;
        }
    }

    @Override
    public String readChannel(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).toString();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).toString();
        return null;
    }

    public ChannelType getChannelType(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).getChannelType();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).getChannelType();
        return null;
    }

    public UUID readChannelId(String name) {
        try {
            return publicChannelNameMap.get(name).getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override // FIXME: 접근 권한 있는 채널만 조회하도록 변경해야 함.
    public List<ResponseChannelDto> readAllChannel() {
        List<ResponseChannelDto> result = new ArrayList<>();

        result.addAll(publicChannelNameMap.values().stream().map(this::requestChannelInfo).toList());
        result.addAll(accessAblePrivateChannel(userState.getUserName()).stream().toList());
        return result;
    }

    private List<ResponseChannelDto> accessAblePrivateChannel(String userName) {
        List<ResponseChannelDto> requestDto = new ArrayList<>();
        privateChannelIdMap.values().stream()
                .filter(channel -> channel.getAccessableUser() != null && channel.getAccessableUser().containsKey(userName))
                .forEach(channel -> requestDto.add(requestChannelInfo(channel)));

        return requestDto;
    }

    private ResponseChannelDto requestChannelInfo(Channel channel) {
        String name = channel.getName();
        UUID id = channel.getId();
        ChannelType type = channel.getChannelType();
        Instant createAt = channel.getCreateAt();
        Instant updateAt = channel.getUpdateAt();
        String createUser = channel.getCreateUser();
        Map<String, UUID> accessableUser = null;
        try {
            accessableUser = channel.getAccessableUser();
        } catch (Exception ignore) {}

        return new ResponseChannelDto(name, id, type, createAt, updateAt, createUser, accessableUser);
    }

    public void invitePrivateServer(String channelName, String userName, UUID userId) {
        privateChannelNameMap.get(channelName).addAccessUser(userName, userId);
    }

    @Override
    public boolean deleteChannel(String name) {
        UUID id = publicChannelNameMap.get(name).getId();

        Path path = resolvePath(id);
        try {
            Files.delete(path);
            publicChannelNameMap.remove(name);
            publicChannelIdMap.remove(id);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    ///
    public boolean isPresentChannel(String name) {
        try {
            publicChannelNameMap.get(publicChannelNameMap.get(name).getName());
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public UUID channelNameToId(String name) {
        try {
            return publicChannelNameMap.get(name).getId();
        } catch (Exception e) {
            return null;
        }
    }

    public String channelIdToName(UUID id) {
        try {
            return publicChannelIdMap.get(id).getName();
        } catch (Exception e) {
            return null;
        }
    }
}
