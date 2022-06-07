package com.softwareverde.tidyduck;

public interface VersionPredictor {

    String predictedNextVersion(final ReleaseItem releaseItem);
}
