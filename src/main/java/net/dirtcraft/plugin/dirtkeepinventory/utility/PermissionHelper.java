package net.dirtcraft.plugin.dirtkeepinventory.utility;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.*;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PermissionHelper {
    public static PermissionHelper INSTANCE = getInstance();
    private static PermissionHelper getInstance(){
        try {
            //return the functioning helper, if the class exists.
            //We do this by using class.forname, which will err if the class is not found, but since it's not a class literal this class will compile
            Class.forName("me.lucko.luckperms.api.LuckPermsApi");
            return new Api4();
        } catch (Exception e) {
            //return a dummy impl. to prevent NPE's. This works because the interface is full of default methods.
            return new PermissionHelper(){};
        }

    }

    public String getServerContext() { return "global"; }

    public int getMetaInt(Player user, String key, int defVal){ return 200; }

    public boolean setMetaInt(String group, String key, int value){ return false; }

    public void setPerm(Player user, String key, boolean value){ }

    public List<String> getGroups(){ return new ArrayList<>();}


    static class Api4 extends PermissionHelper {
        private final LuckPermsApi api = LuckPerms.getApi();
        private final Contexts contexts = api.getContextManager().getStaticContexts();
        private final String serverCtx = getServerContext();

        @Override
        public int getMetaInt(Player user, String key, int defVal) {
            User data = api.getUser(user.getUniqueId());
            if (data == null) return defVal;
            String node = data.getCachedData()
                    .getMetaData(contexts)
                    .getMeta()
                    .getOrDefault(key, String.valueOf(defVal));
            return node.matches("\\d+")? Integer.parseInt(node) : defVal;
        }

        public int getMetaInt(String group, String key, int defVal) {
            Group data = api.getGroup(group);
            if (data == null) return defVal;
            String node = data.getCachedData()
                    .getMetaData(contexts)
                    .getMeta()
                    .getOrDefault(key, String.valueOf(defVal));
            return node.matches("\\d+")? Integer.parseInt(node) : defVal;
        }

        @Override
        public boolean setMetaInt(String group, String key, int value) {
            Group data = api.getGroup(group);
            if (data == null) return false;
            Node oldNode = api.getNodeFactory()
                    .makeMetaNode(key, String.valueOf(getMetaInt(group, key, value)))
                    .setServer(serverCtx)
                    .build();
            Node newNode = api.getNodeFactory()
                    .makeMetaNode(key, String.valueOf(value))
                    .setServer(serverCtx)
                    .build();
            data.unsetPermission(oldNode);
            data.setPermission(newNode);
            api.getGroupManager().saveGroup(data);
            return true;
        }

        @Override
        public void setPerm(Player user, String key, boolean value) {
            User data = api.getUser(user.getUniqueId());
            if (data == null) return;
            Node node = api.buildNode(key)
                    .setValue(value)
                    .setServer(serverCtx)
                    .build();
            data.setPermission(node);
            api.getUserManager().saveUser(data);
        }

        @Override
        public List<String> getGroups(){
            return api.getGroups().stream()
                .map(Group::getName)
                .collect(Collectors.toList());
        }

        @Override
        public String getServerContext() {
            return contexts.getContexts()
                    .getAnyValue("server")
                    .orElse("global");
        }
    }
}
