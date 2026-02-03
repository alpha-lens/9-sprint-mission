package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.NotFound;
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
                publicChannelNameMap.put(channel.getName(), channel);
                publicChannelIdMap.put(channel.getId(), channel);
            } else {
                privateChannelIdMap.put(channel.getId(), channel);
                privateChannelNameMap.put(channel.getName(), channel);
            }

            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] " + e);
        }
        return false;
    }

    @Override
    public boolean save(String oldName, String newName) {
        if(!isPresentChannel(oldName)) return false;
        boolean isPrivate = privateChannelNameMap.containsKey(oldName);
        Path path;
        Channel channel;

        if(isPrivate) {
            channel = privateChannelNameMap.get(oldName);
        } else {
            channel = publicChannelNameMap.get(oldName);
        }

        path = resolvePath(channel.getId());

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
        return "";
    }
    public ChannelType getChannelType(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).getChannelType();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).getChannelType();
        return null;
    }

    public UUID readChannelId(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).getId();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).getId();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }

    @Override
    public List<ResponseChannelDto> readAllChannel(String userName) {
        List<ResponseChannelDto> result = new ArrayList<>();

        /// public
        result.addAll(publicChannelNameMap.values().stream().map(this::requestChannelInfo).toList());

        /// private
        result.addAll(accessAblePrivateChannel(userName).stream().toList());
        return result;
    }

    @Override
    public List<ResponseChannelDto> readAllPrivateChannel(String userName) {
        if(accessAblePrivateChannel(userName).isEmpty())
            throw new NotFound("권한이 있는 Private Channel이 없습니다!");

        return new ArrayList<>(accessAblePrivateChannel(userName).stream().toList());
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
        return publicChannelNameMap.containsKey(name) || privateChannelNameMap.containsKey(name);
    }

    public UUID channelNameToId(String name) {
        if(publicChannelNameMap.containsKey(name)) return publicChannelNameMap.get(name).getId();
        if(privateChannelNameMap.containsKey(name)) return privateChannelNameMap.get(name).getId();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }

    public String channelIdToName(UUID id) {
        if(publicChannelIdMap.containsKey(id)) return publicChannelIdMap.get(id).getName();
        if(privateChannelIdMap.containsKey(id)) return privateChannelIdMap.get(id).getName();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }
}
