package com.softwareverde.tidyduck;

import com.softwareverde.tidyduck.util.Util;

public class IncrementLastIntegerVersionPredictor implements VersionPredictor {

    @Override
    public String predictedNextVersion(final ReleaseItem releaseItem) {
        final String currentVersion = releaseItem.getItemVersion();

        final String[] versionChunks = currentVersion.split("\\.");
        incrementLastInteger(versionChunks);

        return Util.join(".", versionChunks);
    }

    private void incrementLastInteger(final String[] versionChunks) {
        for (int i=versionChunks.length-1; i>=0; i--) {
            final String lastVersionChunk = versionChunks[i];
            if (Util.isInt(lastVersionChunk)) {
                final int lastVersionInt = Util.parseInt(lastVersionChunk);
                final String incrementedVersionChunk = Integer.toString(lastVersionInt+1);
                versionChunks[i] = incrementedVersionChunk;
                // no need to continue
                return;
            }
        }
        // no integer
        throw new IllegalArgumentException("No integer component of version found.");
    }
}
