package com.matrimony.identity.facade;

import com.matrimony.identity.data.FriendRequestType;
import com.matrimony.identity.data.UserPublicProfile;
import com.matrimony.identity.model.FriendRequest;

public interface FriendFacade {

    FriendRequest processFriendRequest(FriendRequestType type, FriendRequest friendRequest);

    UserPublicProfile publicProfile(String requestingUserId, String requestedUserId);
}
