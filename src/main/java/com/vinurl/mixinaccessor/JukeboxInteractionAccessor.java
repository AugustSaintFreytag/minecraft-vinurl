package com.vinurl.mixinaccessor;

import java.util.UUID;

public interface JukeboxInteractionAccessor {

	UUID vinurl$getLastInteractingPlayer();

	void vinurl$setLastInteractingPlayer(UUID playerUuid);

}
