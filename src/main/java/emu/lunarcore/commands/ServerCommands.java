package emu.lunarcore.commands;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import emu.lunarcore.LunarRail;
import emu.lunarcore.util.Utils;

@SuppressWarnings("unused")
public class ServerCommands {
    private static HashMap<String, ServerCommand> list = new HashMap<>();

    static {
        try {
            // Look for classes
            for (Class<?> cls : ServerCommands.class.getDeclaredClasses()) {
                // Get non abstract classes
                if (!Modifier.isAbstract(cls.getModifiers())) {
                    String commandName = cls.getSimpleName().toLowerCase();
                    list.put(commandName, (ServerCommand) cls.newInstance());
                }
            }
        } catch (Exception e) {

        }
    }

    public static void handle(String msg) {
        String[] split = msg.split(" ");

        // End if invalid
        if (split.length == 0) {
            return;
        }

        //
        String first = split[0].toLowerCase();
        ServerCommand c = ServerCommands.list.get(first);

        if (c != null) {
            // Execute
            int len = Math.min(first.length() + 1, msg.length());
            c.execute(msg.substring(len));
        } else {
            LunarRail.getLogger().info("Invalid command!");
        }
    }

    public static abstract class ServerCommand {
        public abstract void execute(String raw);
    }

    // ================ Commands ================

    private static class Account extends ServerCommand {
        @Override
        public void execute(String raw) {
            String[] split = raw.split(" ");

            if (split.length < 2) {
                LunarRail.getLogger().error("Invalid amount of args");
                return;
            }

            emu.lunarcore.game.account.Account account = null;

            String command = split[0].toLowerCase();
            String username = split[1];

            switch (command) {
            case "create":
                if (split.length < 2) { // Should be 3 if passwords were enabled
                    LunarRail.getLogger().error("Invalid amount of args");
                    return;
                }

                // Get password
                //String password = split[2];

                // Reserved player uid
                int reservedUid = 0;
                if (split.length >= 3) {
                    reservedUid = Utils.parseSafeInt(split[2]);
                }

                // Get acocunt from database
                account = LunarRail.getAccountDatabase().getObjectByField(emu.lunarcore.game.account.Account.class, "username", username);

                if (account == null) {
                    // Create account
                    //String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

                    account = new emu.lunarcore.game.account.Account(username);
                    account.setReservedPlayerUid(reservedUid);
                    account.save();

                    LunarRail.getLogger().info("Account created");
                } else {
                    LunarRail.getLogger().error("Account already exists");
                }

                break;
            case "delete":
                account = LunarRail.getAccountDatabase().getObjectByField(emu.lunarcore.game.account.Account.class, "name", username);

                if (account == null) {
                    LunarRail.getLogger().info("Account doesnt exist");
                    return;
                }

                boolean success = LunarRail.getAccountDatabase().delete(account);

                if (success) {
                    LunarRail.getLogger().info("Account deleted");
                }

                break;
            }
        }
    }

}