package com.vivier_technologies.sequencer.receiver;

import com.vivier_technologies.commands.Command;

public interface CommandListener {

    void onCommand(Command command);
}
