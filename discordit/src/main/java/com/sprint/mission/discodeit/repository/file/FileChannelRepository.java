package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.request.RequestCreateChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.*;
import com.sprint.mission.discodeit.exepction.global.Forbidden;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file",
        matchIfMissing = false
)
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
            throw new FailedInit("FileChannelRepository init failed");
        }
    }

    /// interface
    @Override
    public ResponseChannelDto save(RequestCreateChannelDto requestDto) {
        Channel channel = requestDto.toEntity();

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

            return new ResponseChannelDto(
                    channel.getName(),
                    channel.getId(),
                    channel.getChannelType(),
                    channel.getCreateAt(),
                    channel.getUpdateAt(),
                    channel.getCreateUser(),
                    channel.getAccessibleUser());
        } catch (IOException e) {
            throw new FailedCreate("Channel save failed");
        }
    }

    @Override
    public ResponseChannelDto save(RequestUpdateChannelDto requestDto) {
        UUID id = requestDto.id();
        String newName = requestDto.newName();
        if(!isPresentChannel(id)) throw new NotFound("Not found this channel");
        if(privateChannelIdMap.containsKey(id))
            throw new DoNotUpdatePrivateChannel("Do not update private channel");
        Path path;
        Channel channel = publicChannelIdMap.get(id);

        path = resolvePath(channel.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();

            String oldName = publicChannelIdMap.get(id).getName();
            publicChannelNameMap.put(newName, channel);
            publicChannelNameMap.remove(oldName);
            channel.channelUpdater(newName);

            return new ResponseChannelDto(
                    channel.getName(),
                    channel.getId(),
                    channel.getChannelType(),
                    channel.getCreateAt(),
                    channel.getUpdateAt(),
                    channel.getCreateUser(),
                    channel.getAccessibleUser());
        } catch (IOException e) {
            throw new FailedUpdate("Channel update failed");
        }
    }

    @Override
    public ResponseChannelDto findChannel(UUID channelId, UUID userId) {
        if(publicChannelIdMap.containsKey(channelId))
            return requestChannelInfo(publicChannelIdMap.get(channelId));
        if(privateChannelIdMap.containsKey(channelId))
            if (privateChannelIdMap.get(channelId).getAccessibleUser().containsValue(userId))
                return requestChannelInfo(privateChannelIdMap.get(channelId));
            else throw new Forbidden("Cannot accessible this channel!");
        throw new NotFound("Channel not found");
    }

    @Override
    public ChannelType getChannelType(UUID id) {
        if(publicChannelIdMap.containsKey(id))
            return publicChannelIdMap.get(id).getChannelType();
        if(privateChannelIdMap.containsKey(id))
            return privateChannelIdMap.get(id).getChannelType();
        throw new NotFound("ChannelType not found");
    }

    @Override
    public UUID getChannelId(String name) {
        if(publicChannelNameMap.containsKey(name))
            return publicChannelNameMap.get(name).getId();
        if(privateChannelNameMap.containsKey(name))
            return privateChannelNameMap.get(name).getId();

        throw new NotFound("ChannelId not found");
    }

    @Override
    public List<ResponseChannelDto> findAllChannel(UUID userId) {
        List<ResponseChannelDto> result = new ArrayList<>();

        /// public
        result.addAll(publicChannelNameMap.values().stream().map(this::requestChannelInfo).toList());

        /// private
        result.addAll(accessiblePrivateChannel(userId).stream().toList());
        return result;
    }

    @Override
    public List<ResponseChannelDto> findAllPrivateChannel(UUID userId) {
        if(accessiblePrivateChannel(userId).isEmpty())
            throw new NotFound("권한이 있는 Private Channel이 없습니다!");

        return new ArrayList<>(accessiblePrivateChannel(userId).stream().toList());
    }

    @Override
    public List<ResponseChannelDto> accessiblePrivateChannel(UUID userId) {
        List<ResponseChannelDto> requestDto = new ArrayList<>();
        privateChannelIdMap.values().stream()
                .filter(channel -> channel.getChannelType().equals(ChannelType.PRIVATE))
                .filter(channel -> channel.getAccessibleUser().containsValue(userId))
                .forEach(channel -> requestDto.add(requestChannelInfo(channel)));

        return requestDto;
    }

    @Override
    public ResponseChannelDto requestChannelInfo(Channel channel) {
        String name = channel.getName();
        UUID id = channel.getId();
        ChannelType type = channel.getChannelType();
        Instant createAt = channel.getCreateAt();
        Instant updateAt = channel.getUpdateAt();
        String createUser = channel.getCreateUser();
        Map<String, UUID> accessableUser = null;
        try {
            accessableUser = channel.getAccessibleUser();
        } catch (Exception ignore) {}

        return new ResponseChannelDto(name, id, type, createAt, updateAt, createUser, accessableUser);
    }

    @Override
    public void includePrivateChannel(String channelName, String userName, UUID userId) {
        privateChannelNameMap.get(channelName).addAccessibleUser(userName, userId);
    }

    @Override
    public void excludePrivateChannel(String channelName, String userName) {
        privateChannelNameMap.get(channelName).removeAccessibleUser(userName);
    }

    @Override
    public boolean deleteChannel(UUID id) {
        String name;
        boolean isPrivate = privateChannelIdMap.containsKey(id);

        if(isPrivate){
            name = privateChannelIdMap.get(id).getName();
        } else {
            name = publicChannelIdMap.get(id).getName();
        }

        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
            if(isPrivate){
                privateChannelNameMap.remove(name);
                privateChannelIdMap.remove(id);
            } else {
                publicChannelNameMap.remove(name);
                publicChannelIdMap.remove(id);
            }
            return true;
        } catch (IOException e) {
            throw new FailedDelete("Channel delete failed");
        }
    }

    @Override
    public void deleteAllChannel(String name) {
        List<Channel> channels = privateChannelNameMap.values().stream().filter(channel -> channel.getCreateUser().equals(name)).toList();

        channels.forEach(channel -> deleteChannel(channel.getId()));
    }


    @Override
    public boolean isPresentChannel(UUID id) {
        return publicChannelIdMap.containsKey(id) || privateChannelIdMap.containsKey(id);
    }

    @Override
    public boolean isCreatePrivateChannel(String name) {
        return !privateChannelNameMap.values().stream().filter(channel -> channel.getCreateUser().equals(name)).toList().isEmpty();
    }

    @Override
    public boolean findChannelCreator(UUID id, String userName) {
        if(publicChannelIdMap.containsKey(id))
            return publicChannelIdMap.get(id).getCreateUser().equals(userName);
        if(privateChannelIdMap.containsKey(id))
            return privateChannelIdMap.get(id).getCreateUser().equals(userName);

        return false;
    }

    @Override
    public UUID channelNameToId(String name) {
        if(publicChannelNameMap.containsKey(name)) return publicChannelNameMap.get(name).getId();
        if(privateChannelNameMap.containsKey(name)) return privateChannelNameMap.get(name).getId();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }

    @Override
    public String channelIdToName(UUID id) {
        if(publicChannelIdMap.containsKey(id)) return publicChannelIdMap.get(id).getName();
        if(privateChannelIdMap.containsKey(id)) return privateChannelIdMap.get(id).getName();

        throw new NotFound("해당 채널을 찾을 수 없습니다");
    }
}
