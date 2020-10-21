package co.gergely.vasalka.inerfaces;

import co.gergely.vasalka.model.ChatRoom;

public interface OnHandleChat {
    void onChatOpen(ChatRoom room);

    void onChatDelete(ChatRoom room);
}
