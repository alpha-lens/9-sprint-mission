package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileChannelService implements ChannelService {
    private final Map<String, Channel> channelNameMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private FileChannelService() {
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
                        channelNameMap.put(channel.getName(), channel);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static class Holder {
        private static final FileChannelService INSTANCE = new FileChannelService();
    }
    public static FileChannelService getInstance() {
        return Holder.INSTANCE;
    }

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void createChannel(Scanner sc) {
        System.out.println("사용하려는 채널명이 무엇인가요?");
        String name = sc.nextLine().trim();

        if (check(name) != null) {
            System.out.println("이미 존재하는 채널명이에요!");
            return;
        }

        Channel channel = new Channel(name);
        Path path = resolvePath(channel.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        channelNameMap.put(name, channel);
        System.out.println("잘 들어갔어요!");
    }

    @Override
    public void readAllChannel() {
        if (channelNameMap.isEmpty()) {
            System.out.println("채널이 존재하지 않습니다.");
            return;
        }

        channelNameMap.values().forEach(System.out::println);

        System.out.println("현재 총 채널수 : " + channelNameMap.size());
    }

    @Override
    public void updateChannel(Scanner sc) {
        System.out.println("변경하고자 하는 채널명을 알려주세요");
        String name = sc.nextLine();

        Channel result = check(name);
        Channel channel = null;
        Path path = resolvePath(result.getId());

        if (result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return;
        }

        System.out.println("현재 채널명 : " + name);
        System.out.println("무엇으로 변경하고 싶은가요? ");

        name = sc.nextLine();

        try(FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis)) {
            channel = (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        channelNameMap.put(name, result);
        channelNameMap.remove(result.getName());
        result.channelUpdater(name);

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(result.getName());
        System.out.println("잘 변경되었어요!");
    }

    @Override
    public void deleteChannel(Scanner sc) {
        String inputChannelName;
        int n;
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.out.println("[Warning!] 계속 진행하려면 아무 숫자나 입력해주세요");

        n = sc.nextInt();
        sc.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        System.out.print("삭제하려는 채널명을 알려주세요: ");
        inputChannelName = sc.nextLine();

        if (channelNameMap.get(inputChannelName) == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return;
        }

        Path path = resolvePath(channelNameMap.get(inputChannelName).getId());
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        channelNameMap.remove(inputChannelName);
        System.out.println("해당 채널이 삭제되었습니다.");
    }

    @Override
    public void isChannelName(Scanner sc) {
        System.out.println("조회하고자 하는 채널명을 입력해주세요");
        String name = sc.nextLine();
        Channel result = check(name);

        if (result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return;
        }

        System.out.println("해당 채널에 대한 정보를 알려드립니다.");
        System.out.println(result);
    }

    /// 메시지 발송용
    @Override
    public Channel isChannelName(String name) {
        Channel result = check(name);

        if (result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
        return result;
    }

    /// UUID to Name
    @Override
    public String isChannelName(UUID id) {
        Channel result = check(id);

        if (result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
        return result.getName();
    }

    @Override
    public Channel check(String name) {
        try {
            return channelNameMap.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    /// 채널ID로도 관리하는 형태를 만드는게 좋을까?
    @Override
    public Channel check(UUID id) {
        return channelNameMap.values().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }
}
