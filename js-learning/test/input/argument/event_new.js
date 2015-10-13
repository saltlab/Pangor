if (!isHistory) powGlobals.engine.server.on('listening', function(remSel,remPlaylist,remEngine) {
	return function() {
		if (tempSel != remSel) {
			killEngine(remEngine);
			return;
		}
		powGlobals.engine = remEngine;
		if (remPlaylist["0"]) engage(0,remPlaylist,remSel);
		else engage();
		powGlobals.serverReady = 1;
	}
}(tempSel,rememberPlaylist,powGlobals.engine));
else if (isHistory) powGlobals.engine.server.on('listening', function(remSel,remHistory,remEngine) {
	return function() {
		if (tempSel != remSel) {
			killEngine(remEngine);
			return;
		}
		powGlobals.engine = remEngine;
		engage(remHistory);
		powGlobals.serverReady = 1;
	}
}(tempSel,targetHistory,powGlobals.engine));