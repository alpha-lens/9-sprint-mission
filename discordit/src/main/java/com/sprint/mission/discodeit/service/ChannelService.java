package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.FindChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;

import java.util.List;

public interface ChannelService {
    boolean isPresent(String name);
    boolean create(String type, String name);
    FindChannelDto find(String name);
    List<ResponseChannelDto> findAll();
    boolean update(String oldName, String newName);
    boolean delete(String name);
}
