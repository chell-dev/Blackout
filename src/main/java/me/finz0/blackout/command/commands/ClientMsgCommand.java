package me.finz0.blackout.command.commands;

import me.finz0.blackout.command.Command;

public class ClientMsgCommand extends Command {

    public ClientMsgCommand() {
        super(new String[]{"clientmessage"}, "clientmessage <Text>");
    }

    @Override
    public void exec(String args) throws Exception {
        sendClientMessage(args, true);
    }
}
