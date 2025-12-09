package com.vinurl.mixinaccessor;

import java.util.UUID;

public interface JukeboxInteractionAccessor {

	void vinurl$setRecordStartTick(long tick);

	UUID vinurl$getLastInteractingPlayer();

	void vinurl$setLastInteractingPlayer(UUID playerUuid);

}
