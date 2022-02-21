package com.matrimony.identity.facade;

import com.matrimony.identity.data.FriendRequestType;
import com.matrimony.identity.model.FriendRequest;

public interface FriendRequestFacade {

    FriendRequest processFriendRequest(FriendRequestType type, FriendRequest friendRequest);
}
