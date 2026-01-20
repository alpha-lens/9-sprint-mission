package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Input;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
    private final Map<UUID, User> usersMap;
    private final Map<String, UUID> usersName;
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private FileUserService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /// 파일에서 user값을 불러와, hashMap으로 다시 저장하는 과정
        /// concurrentHashMap을 사용하는 방법은 모르겠다.
        try {
            usersMap = Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toMap(User::getId, user -> user));
            usersName = Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toMap(User::getName, User::getId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static class Holder {
        private static final FileUserService INSTANCE = new FileUserService();
    }
    public static FileUserService getInstance() {return Holder.INSTANCE;}

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void createUser(Scanner sc) {
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        String name = sc.nextLine().trim();

        if (usersName.containsKey(name)) {
            System.out.println("이미 존재하는 사용자명입니다.");
            return;
        }

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        String pw = sc.nextLine();
        User user = new User(name, pw);

        usersName.put(name, user.getId());
        usersMap.put(user.getId(), user);
        Path path = resolvePath(user.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("사용자 " + name + "님이 추가되었습니다.");
    }

    /// Update
    @Override
    public void updateUser(Scanner sc) {
        System.out.println("====================");
        System.out.println("사용자 변경 메뉴입니다.");
        User checkUpdateUser = check(sc, "변경");
        if(checkUpdateUser == null) {
            System.out.println("일치하는 사용자가 없습니다.");
            return;
        }

        while(true) {
            System.out.println("변경하지 않으실 필드에는 엔터를 눌러주시기 바랍니다.");

            String reName = Input.inputUpdateField(sc, "사용자명", "\\S+", checkUpdateUser.getName());
            String rePassword = Input.inputUpdateField(sc, "비밀번호", "\\S+", null);
            String reMail = Input.inputUpdateField(sc, "이메일", "\\S+@\\S+\\.\\S+", checkUpdateUser.getEmail());
            String rePhoneNumber = Input.inputUpdateField(sc, "전화번호", "^\\d{10,11}$", checkUpdateUser.getPhoneNumber());

            System.out.println("이대로 진행하시겠습니까?");
            System.out.println("맞으면 y, 다시 입력하려면 re");
            System.out.println("취소하려면 아무 키나 입력해주시기 바랍니다.");

            String finalCheckIsContinue = sc.nextLine();
            switch (finalCheckIsContinue.toLowerCase()){
                case "y":
                    Path path = resolvePath(checkUpdateUser.getId());
                    User user = null;

                    if(Files.exists(path)) {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            user = (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    checkUpdateUser.updateUser(reName, rePassword, reMail, rePhoneNumber);
                    user.updateUser(reName, rePassword, reMail
                    , rePhoneNumber);

                    try(FileOutputStream fos = new FileOutputStream(path.toFile());
                        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                        oos.writeObject(user);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("완료");
                    return;
                case "re":
                    continue;
                default:
                    return;
            }
        }
    }

    /// Read
    public User getUserId(UUID id) {
        return usersMap.get(id);
    }

    @Override
    public void getUserName(Scanner sc) {
        System.out.println("조회하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();

        if (usersName.get(name) == null) {
            System.out.println("조회하고자 하는 사용자가 없습니다.");
            return;
        }

        User user = usersMap.get(usersName.get(name));

        /// 여기서 파일 불러와서 조회하는 로직 작성.
        User oisUser;
        try (
                FileInputStream fis = new FileInputStream(resolvePath(user.getId()).toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            /// 당장은 안 쓰고 있긴 한데... 써야 하나?
            /// 테스트용으로 추가해둘까?
            oisUser = (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("조회하고자 하는 사용자가 없습니다.");
            return;
        }

        /// 일단 되는지 확인하기 위해서 섞어서 쓰긴 했는데...
        System.out.println("====================");
        System.out.println("사용자ID : " + oisUser.getId());
        System.out.println("사용자명 : " + oisUser.getName());
        System.out.println("이메일 : " + oisUser.getEmail());
        System.out.println("전화번호 : " + user.getPhoneNumber());
        System.out.println("생성일 : " + user.getCreateAt());
        System.out.println("수정일 : " + user.getUpdateAt());
        System.out.println("====================");
    }

    public User getUserByName(String name) {
        try {
            return usersMap.get(usersName.get(name));
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserByName(UUID id) {
        return usersMap.get(id).getName();
    }

    @Override
    public void getAllUserName() {
        if (usersMap.isEmpty()) {
            System.out.println("사용자가 없습니다.");
            return;
        }

//        List<User> userList = serToUserList();

        usersMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> {
            System.out.println("====================");
            System.out.println("사용자ID : " + user.getId());
            System.out.println("사용자명 : " + user.getName());
            System.out.println("이메일 : " + user.getEmail());
            System.out.println("전화번호 : " + user.getPhoneNumber());
            System.out.println("생성일 : " + user.getCreateAt());
            System.out.println("수정일 : " + user.getUpdateAt());
            System.out.println("====================");
        });
        System.out.println("현재 총 사용자 : " + usersMap.size());
    }

    /// Delete
    @Override
    public void deleteUser(Scanner sc) {
        int n;
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.out.println("[Warning!] 계속 진행하시려면 아무 숫자나 입력해주세요.");

        n = sc.nextInt();
        sc.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        User target = check(sc, "삭제");

        if (target == null) {
            System.out.println("일치하는 계정을 찾을 수 없습니다.");
            return;
        }

        Path path = resolvePath(target.getId());
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        usersMap.remove(target.getId());
        usersName.remove(target.getName());

        System.out.println("계정이 삭제되었습니다.");
    }

    private User check(Scanner sc, String work) {
        System.out.println(work + "하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();

        User user = usersMap.get(usersName.get(name));

        if (user == null) return null;

        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();

        if (!user.getPw().equals(pw)) return null;

        return user;
    }

    private List<User> serToUserList() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
