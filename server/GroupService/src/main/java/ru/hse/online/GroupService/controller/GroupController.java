package ru.hse.online.GroupService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.hse.online.GroupService.data.GroupManager;
import ru.hse.online.GroupService.data.Invite;
import ru.hse.online.GroupService.data.Location;

import java.util.*;

@Controller
public class GroupController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GroupManager groupManager = new GroupManager();

    static private final ObjectMapper mapper = new ObjectMapper();

    public GroupController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/start")
    @SendToUser("/queue/startWalk")
    public Long registerNewUser(String username) {
        username = username.substring(1, username.length() - 1);
        return groupManager.addUser(username);
    }

    @MessageMapping("/stop")
    @SendToUser("/queue/endWalk")
    public String unregisterUser(String username) {
        username = username.substring(1, username.length() - 1);
        groupManager.removeUser(username);
        return username;
    }

    @MessageMapping("/invite")
    public void invite(Invite invite) {
        messagingTemplate.convertAndSendToUser(invite.toWho, "/msg", "{\"from\": \"" + invite.fromWho + "\"}");
    }

    @MessageMapping("/joinGroup")
    public void joinGroup(Invite invite) {
        groupManager.joinUserGroups(invite.toWho, invite.fromWho);
    }

    @MessageMapping("/quitGroup")
    public void quitGroup(String username) {
        username = username.substring(1, username.length() - 1);
        groupManager.moveUserToNewGroup(username);
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    private record FromUsernameAndLocation(String from, Location location) {
        public String toJson() {
            return "{\"from\": " + "\"" + from + "\", \"lat\": " + location.lat + ", \"lng\": " + location.lng + "}";
        }
    }

    @MessageMapping("/updateLocation")
    public void updateLocation(String data) throws JsonProcessingException {
        FromUsernameAndLocation usernameAndLocation = mapper.readValue(data, FromUsernameAndLocation.class);

        Set<String> group = groupManager.getGroup(groupManager.getGroupId(usernameAndLocation.from));
        for (String to : group) {
            if (!Objects.equals(usernameAndLocation.from, to)) {
                messagingTemplate.convertAndSendToUser(to, "/msg", usernameAndLocation.toJson());
            }
        }
    }
}
