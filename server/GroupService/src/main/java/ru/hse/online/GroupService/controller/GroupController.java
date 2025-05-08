package ru.hse.online.GroupService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.hse.online.GroupService.data.Invite;
import ru.hse.online.GroupService.data.Location;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller public class GroupController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Long> userToWalkId;
    private final Map<Long, Set<String>> walkIdToUserGroup;
    private long walkId = 0;

    public GroupController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.userToWalkId = new ConcurrentHashMap<>();
        this.walkIdToUserGroup = new ConcurrentHashMap<>();
    }

    @MessageMapping("/start")
    @SendToUser("/queue/startWalk")
    public Long registerNewUser(String username) {
        if (!userToWalkId.containsKey(username)) {
            userToWalkId.put(username, walkId);
            Set<String> newWalk = new HashSet<>();
            newWalk.add(username);
            walkIdToUserGroup.put(walkId, newWalk);
            ++walkId;
        }
        return userToWalkId.get(username);
    }

    @MessageMapping("/stop")
    @SendToUser("/queue/endWalk")
    public String unregisterUser(String username) {
        userToWalkId.remove(username);
        return username;
    }

    @MessageMapping("/invite")
    public void invite(Invite invite) {
        System.out.println("Sending invite from " + invite.fromWho + " to " + invite.toWho);
        messagingTemplate.convertAndSendToUser(invite.toWho, "/msg", "{\"from\": \"" + invite.fromWho + "\"}");
    }

    @MessageMapping("/joinGroup")
    public void joinGroup(Invite invite) {
        userToWalkId.put(invite.toWho, userToWalkId.get(invite.fromWho));
    }

    @MessageMapping("/quitGroup")
    public void quitGroup(String username) {
        userToWalkId.put(username, walkId++);
    }

    private record FromUsernameAndLocation(String from, Location location) {
    }

    // WTF
    @MessageMapping("/updateLocation")
    public void updateLocation(String data) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        FromUsernameAndLocation usernameAndLocation = mapper.readValue(data, FromUsernameAndLocation.class);
        String from = usernameAndLocation.from;
        Location location = usernameAndLocation.location;

        assert userToWalkId.keySet().iterator().next().equals(from);
        assert userToWalkId.containsKey(from);
        System.out.println(userToWalkId.values());
        System.out.println(from);
        System.out.println(userToWalkId);
        System.out.println(userToWalkId.get(from));
        System.out.println(walkIdToUserGroup);
        System.out.println(walkIdToUserGroup.get(userToWalkId.get(from)));

        for (String to : walkIdToUserGroup.get(userToWalkId.get(from))) {
            if (!to.equals(from)) {
                messagingTemplate.convertAndSendToUser(to, "/msg", location);
            }
        }
    }

}

