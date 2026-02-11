package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.RequestCreateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.DoNotUpdatePrivateChannel;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.exepction.global.Forbidden;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFChannelRepository implements ChannelRepository {
    private final Map<String, Channel> publicChannelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> publicChannelIdMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> privateChannelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> privateChannelIdMap = new ConcurrentHashMap<>();

    /// interface
    @Override
    public ResponseChannelDto save(RequestCreateChannelDto requestDto) {
        Channel channel = requestDto.toEntity();
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
    }

    @Override
    public ResponseChannelDto save(RequestUpdateChannelDto requestDto) {
        UUID id = requestDto.id();
        String newName = requestDto.newName();
        if(!isPresentChannel(id))  throw new NotFound("Not found this channel");
        if(privateChannelIdMap.containsKey(id))
            throw new DoNotUpdatePrivateChannel("Do not update private channel");
        Channel channel = publicChannelIdMap.get(id);
        String oldName = channel.getName();
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

        if(isPrivate){
            privateChannelNameMap.remove(name);
            privateChannelIdMap.remove(id);
        } else {
            publicChannelNameMap.remove(name);
            publicChannelIdMap.remove(id);
        }
        return true;
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
